#include <jni.h>

#include <atomic>
#include <cstring>
#include <mutex>
#include <string>
#include <vector>

#include "llama.h"
#include "ggml-backend.h"

static llama_model * g_model = nullptr;
static std::mutex g_model_mutex;
static std::atomic<bool> g_stop_requested(false);

static bool llama_abort_callback(void *) {
    return g_stop_requested.load();
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_offlineai_app_ai_OfflineAiNative_hello(
        JNIEnv * env,
        jobject) {

    return env->NewStringUTF(
            "Offline AI native engine is ready."
    );
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_offlineai_app_ai_OfflineAiNative_loadModel(
        JNIEnv * env,
        jobject,
        jstring j_model_path) {

    if (j_model_path == nullptr) {
        return env->NewStringUTF(
                "Error: model path is null."
        );
    }

    const char * model_path =
            env->GetStringUTFChars(
                    j_model_path,
                    nullptr
            );

    if (model_path == nullptr) {
        return env->NewStringUTF(
                "Error: unable to read model path."
        );
    }

    std::lock_guard<std::mutex> lock(g_model_mutex);

    if (g_model != nullptr) {
        env->ReleaseStringUTFChars(
                j_model_path,
                model_path
        );

        return env->NewStringUTF(
                "Offline AI model is already loaded."
        );
    }

    ggml_backend_load_all();

    llama_model_params model_params =
            llama_model_default_params();

    model_params.n_gpu_layers = 0;

    g_model =
            llama_model_load_from_file(
                    model_path,
                    model_params
            );

    env->ReleaseStringUTFChars(
            j_model_path,
            model_path
    );

    if (g_model == nullptr) {
        return env->NewStringUTF(
                "Error: unable to load Qwen model."
        );
    }

    return env->NewStringUTF(
            "Offline AI model loaded."
    );
}

extern "C"
JNIEXPORT void JNICALL
Java_com_offlineai_app_ai_OfflineAiNative_stopGeneration(
        JNIEnv *,
        jobject) {

    g_stop_requested.store(true);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_offlineai_app_ai_OfflineAiNative_generate(
        JNIEnv * env,
        jobject,
        jstring j_prompt,
        jint context_size,
        jint max_tokens,
        jint threads) {

    if (j_prompt == nullptr) {
        return env->NewStringUTF(
                "Error: prompt is null."
        );
    }

    const char * prompt_text =
            env->GetStringUTFChars(
                    j_prompt,
                    nullptr
            );

    if (prompt_text == nullptr) {
        return env->NewStringUTF(
                "Error: unable to read prompt."
        );
    }

    std::lock_guard<std::mutex> lock(g_model_mutex);

    if (g_model == nullptr) {
        env->ReleaseStringUTFChars(
                j_prompt,
                prompt_text
        );

        return env->NewStringUTF(
                "Error: AI model is not loaded."
        );
    }

    g_stop_requested.store(false);

    const llama_vocab * vocab =
            llama_model_get_vocab(g_model);


    const int prompt_length =
            -llama_tokenize(
                    vocab,
                    prompt_text,
                    strlen(prompt_text),
                    nullptr,
                    0,
                    true,
                    true
            );

    if (prompt_length <= 0) {
        env->ReleaseStringUTFChars(
                j_prompt,
                prompt_text
        );

        return env->NewStringUTF(
                "Error: unable to tokenize prompt."
        );
    }

    std::vector<llama_token> prompt_tokens(
            prompt_length
    );

    const int tokenized =
            llama_tokenize(
                    vocab,
                    prompt_text,
                    strlen(prompt_text),
                    prompt_tokens.data(),
                    prompt_tokens.size(),
                    true,
                    true
            );

    if (tokenized < 0) {
        env->ReleaseStringUTFChars(
                j_prompt,
                prompt_text
        );

        return env->NewStringUTF(
                "Error: failed to tokenize prompt."
        );
    }

    const int safe_max_tokens =
            max_tokens > 0 ? max_tokens : 128;

    const int requested_context =
            context_size > 0 ? context_size : 2048;

    const int safe_context =
            requested_context >
            prompt_length + safe_max_tokens
                ? requested_context
                : prompt_length + safe_max_tokens + 1;

    llama_context_params ctx_params =
            llama_context_default_params();

    ctx_params.n_ctx = safe_context;

    ctx_params.n_batch =
            prompt_length > 512
                ? 512
                : prompt_length;

    ctx_params.n_threads =
            threads > 0 ? threads : 4;

    ctx_params.n_threads_batch =
            threads > 0 ? threads : 4;

    ctx_params.abort_callback =
            llama_abort_callback;

    ctx_params.abort_callback_data =
            nullptr;

    llama_context * ctx =
            llama_init_from_model(
                    g_model,
                    ctx_params
            );

    if (ctx == nullptr) {
        env->ReleaseStringUTFChars(
                j_prompt,
                prompt_text
        );

        return env->NewStringUTF(
                "Error: failed to create llama context."
        );
    }

    auto sampler_params =
            llama_sampler_chain_default_params();

    sampler_params.no_perf = true;

    llama_sampler * sampler =
            llama_sampler_chain_init(
                    sampler_params
            );

    llama_sampler_chain_add(
            sampler,
            llama_sampler_init_greedy()
    );

    llama_batch batch =
            llama_batch_get_one(
                    prompt_tokens.data(),
                    prompt_tokens.size()
            );

    if (llama_decode(ctx, batch) != 0) {

        const bool stopped =
                g_stop_requested.load();

        llama_sampler_free(sampler);
        llama_free(ctx);

        env->ReleaseStringUTFChars(
                j_prompt,
                prompt_text
        );

        if (stopped) {
            return env->NewStringUTF(
                    "__STOPPED__"
            );
        }

        return env->NewStringUTF(
                "Error: failed to evaluate prompt."
        );
    }

    std::string result;

    for (int i = 0; i < safe_max_tokens; ++i) {

        if (g_stop_requested.load()) {
            llama_sampler_free(sampler);
            llama_free(ctx);

            env->ReleaseStringUTFChars(
                    j_prompt,
                    prompt_text
            );

            return env->NewStringUTF(
                    "__STOPPED__"
            );
        }

        llama_token token =
                llama_sampler_sample(
                        sampler,
                        ctx,
                        -1
                );

        if (llama_vocab_is_eog(vocab, token)) {
            break;
        }

        char buffer[256];

        const int length =
                llama_token_to_piece(
                        vocab,
                        token,
                        buffer,
                        sizeof(buffer),
                        0,
                        true
                );

        if (length < 0) {
            break;
        }

        result.append(
                buffer,
                length
        );

        batch =
                llama_batch_get_one(
                        &token,
                        1
                );

        if (llama_decode(ctx, batch) != 0) {

            const bool stopped =
                    g_stop_requested.load();

            llama_sampler_free(sampler);
            llama_free(ctx);

            env->ReleaseStringUTFChars(
                    j_prompt,
                    prompt_text
            );

            if (stopped) {
                return env->NewStringUTF(
                        "__STOPPED__"
                );
            }

            return env->NewStringUTF(
                    "Error: generation stopped."
            );
        }
    }

    llama_sampler_free(sampler);
    llama_free(ctx);

    env->ReleaseStringUTFChars(
            j_prompt,
            prompt_text
    );

    if (g_stop_requested.load()) {
        return env->NewStringUTF(
                "__STOPPED__"
        );
    }

    if (result.empty()) {
        result = "No response generated.";
    }

    return env->NewStringUTF(
            result.c_str()
    );
}

extern "C"
JNIEXPORT void JNICALL
Java_com_offlineai_app_ai_OfflineAiNative_unloadModel(
        JNIEnv *,
        jobject) {

    std::lock_guard<std::mutex> lock(
            g_model_mutex
    );

    if (g_model != nullptr) {
        llama_model_free(g_model);
        g_model = nullptr;
    }
}
