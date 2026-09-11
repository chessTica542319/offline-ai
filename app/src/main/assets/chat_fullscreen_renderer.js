function decodeBase64(value) {

    const binary =
        atob(value);

    const bytes =
        new Uint8Array(
            binary.length
        );

    for (
        let i = 0;
        i < binary.length;
        i++
    ) {
        bytes[i] =
            binary.charCodeAt(i);
    }

    return new TextDecoder(
        "utf-8"
    ).decode(bytes);
}


function setCode(
    languageEncoded,
    codeEncoded
) {

    const language =
        decodeBase64(
            languageEncoded
        ).trim();

    const code =
        decodeBase64(
            codeEncoded
        );

    const content =
        document.getElementById(
            "content"
        );

    const wrapper =
        document.createElement(
            "div"
        );

    wrapper.className =
        "code-wrapper";

    const body =
        document.createElement(
            "div"
        );

    body.className =
        "code-body";

    const pre =
        document.createElement(
            "pre"
        );

    const codeElement =
        document.createElement(
            "code"
        );

    if (
        language !== ""
    ) {
        codeElement.className =
            "language-" +
            language;
    }

    if (
        language !== "" &&
        hljs.getLanguage(
            language
        )
    ) {

        codeElement.innerHTML =
            hljs.highlight(
                code,
                {
                    language:
                        language,
                    ignoreIllegals:
                        true
                }
            ).value;

    } else {

        codeElement.innerHTML =
            hljs.highlightAuto(
                code
            ).value;
    }

    pre.appendChild(
        codeElement
    );

    body.appendChild(
        pre
    );

    wrapper.appendChild(
        body
    );

    content.innerHTML = "";

    content.appendChild(
        wrapper
    );
}
