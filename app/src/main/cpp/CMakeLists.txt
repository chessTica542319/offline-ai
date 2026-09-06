cmake_minimum_required(VERSION 3.22.1)

project("offlineai")

set(LLAMA_DIR "/data/data/com.termux/files/home/llama.cpp")
set(JNI_LIBS_DIR "${CMAKE_CURRENT_LIST_DIR}/../jniLibs/arm64-v8a")

add_library(offlineai_jni SHARED
    offlineai_jni.cpp
)

target_include_directories(offlineai_jni PRIVATE
    "${LLAMA_DIR}/include"
    "${LLAMA_DIR}/ggml/include"
)

add_library(llama SHARED IMPORTED)
set_target_properties(llama PROPERTIES
    IMPORTED_LOCATION "${JNI_LIBS_DIR}/libllama.so"
)

add_library(ggml SHARED IMPORTED)
set_target_properties(ggml PROPERTIES
    IMPORTED_LOCATION "${JNI_LIBS_DIR}/libggml.so"
)

add_library(ggml-cpu SHARED IMPORTED)
set_target_properties(ggml-cpu PROPERTIES
    IMPORTED_LOCATION "${JNI_LIBS_DIR}/libggml-cpu.so"
)

add_library(ggml-base SHARED IMPORTED)
set_target_properties(ggml-base PROPERTIES
    IMPORTED_LOCATION "${JNI_LIBS_DIR}/libggml-base.so"
)

target_link_libraries(offlineai_jni
    llama
    ggml
    ggml-cpu
    ggml-base
    log
)
