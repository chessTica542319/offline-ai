function languageLabel(value) {

    const language =
        value
            .trim()
            .toLowerCase();

    const labels = {
        python: "Python",
        py: "Python",
        java: "Java",
        kotlin: "Kotlin",
        kt: "Kotlin",
        javascript: "JavaScript",
        js: "JavaScript",
        typescript: "TypeScript",
        ts: "TypeScript",
        html: "HTML",
        css: "CSS",
        xml: "XML",
        json: "JSON",
        bash: "Bash",
        sh: "Shell",
        shell: "Shell",
        sql: "SQL",
        c: "C",
        cpp: "C++",
        csharp: "C#",
        cs: "C#",
        rust: "Rust",
        go: "Go",
        php: "PHP",
        ruby: "Ruby"
    };

    return labels[language] ||
        (
            language.length > 0
                ? language.charAt(0).toUpperCase() +
                  language.slice(1)
                : "Code"
        );
}


function normalizeCode(code) {

    let value =
        code.replace(
            /\r\n/g,
            "\n"
        );

    value =
        value.replace(
            /^\n+/,
            ""
        );

    value =
        value.replace(
            /\n+$/,
            ""
        );

    const lines =
        value.split("\n");

    let minimumIndent = null;

    lines.forEach(function(line) {

        if (
            line.trim().length === 0
        ) {
            return;
        }

        const match =
            line.match(
                /^[ \t]+/
            );

        if (!match) {
            minimumIndent = 0;
            return;
        }

        const indent =
            match[0]
                .replace(
                    /\t/g,
                    "    "
                )
                .length;

        if (
            minimumIndent === null ||
            indent < minimumIndent
        ) {
            minimumIndent =
                indent;
        }
    });

    if (
        minimumIndent &&
        minimumIndent > 0
    ) {

        value =
            lines
                .map(function(line) {

                    return line.replace(
                        new RegExp(
                            "^ {0," +
                            minimumIndent +
                            "}"
                        ),
                        ""
                    );

                })
                .join("\n");
    }

    return value;
}


function extractCodeBlocks(text) {

    const normalized =
        text.replace(
            /\r\n/g,
            "\n"
        );

    const pattern =
        /(^|\n)[ \t]*(```|''')([^\n`]*)\n([\s\S]*?)(?:\n[ \t]*\2)(?=\n|$)/g;

    const parts = [];

    let lastIndex = 0;
    let match;

    while (
        (match =
            pattern.exec(
                normalized
            )) !== null
    ) {

        const normalText =
            normalized.substring(
                lastIndex,
                match.index +
                (
                    match[1]
                        ? match[1].length
                        : 0
                )
            );

        if (
            normalText.trim().length > 0
        ) {

            parts.push({
                type: "text",
                value: normalText
            });
        }

        const language =
            match[3]
                .trim();

        const code =
            normalizeCode(
                match[4]
            );

        parts.push({
            type: "code",
            language: language,
            code: code
        });

        lastIndex =
            pattern.lastIndex;
    }

    const remaining =
        normalized.substring(
            lastIndex
        );

    if (
        remaining.trim().length > 0
    ) {

        parts.push({
            type: "text",
            value: remaining
        });
    }

    if (
        parts.length === 0
    ) {

        parts.push({
            type: "text",
            value: normalized
        });
    }

    return parts;
}

function appendTextPart(
    root,
    text
) {

    const container =
        document.createElement(
            "div"
        );

    const normalText =
        text.replace(
            /^[ \t]+(?=\S)/gm,
            ""
        );

    const preparedText =
        prepareMathText(
            normalText
        );

    container.innerHTML =
        marked.parse(
            preparedText
        );

    restoreMathText(
        container
    );

    while (
        container.firstChild
    ) {

        root.appendChild(
            container.firstChild
        );
    }
}


function appendCodePart(
    root,
    language,
    source
) {

    const wrapper =
        document.createElement(
            "div"
        );

    wrapper.className =
        "code-wrapper";

    const header =
        document.createElement(
            "div"
        );

    header.className =
        "code-header";

    const languageElement =
        document.createElement(
            "span"
        );

    languageElement.className =
        "code-language";

    languageElement.textContent =
        languageLabel(
            language
        );

    const actions =
        document.createElement(
            "div"
        );

    actions.className =
        "code-actions";

    const copy =
        document.createElement(
            "button"
        );

    copy.className =
        "code-action";

    copy.type =
        "button";

    copy.textContent =
        "⧉";

    copy.title =
        "Copy code";

    copy.addEventListener(
        "click",
        function() {

            AndroidClipboard.copyCode(
                source
            );

            copy.textContent =
                "✓";

            setTimeout(
                function() {

                    copy.textContent =
                        "⧉";

                },
                1200
            );
        }
    );

    const fullScreen =
        document.createElement(
            "button"
        );

    fullScreen.className =
        "code-action";

    fullScreen.type =
        "button";

    fullScreen.textContent =
        "⛶";

    fullScreen.title =
        "Full screen";

    fullScreen.addEventListener(
        "click",
        function() {

            AndroidCode.openFullScreen(
                languageLabel(
                    language
                ),
                source
            );
        }
    );

    actions.appendChild(
        copy
    );

    actions.appendChild(
        fullScreen
    );

    header.appendChild(
        languageElement
    );

    header.appendChild(
        actions
    );

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

    const code =
        document.createElement(
            "code"
        );

    code.textContent =
        source;

    if (
        language.trim()
    ) {

        code.className =
            "language-" +
            language
                .trim()
                .toLowerCase();
    }

    pre.appendChild(
        code
    );

    body.appendChild(
        pre
    );

    wrapper.appendChild(
        header
    );

    wrapper.appendChild(
        body
    );

    root.appendChild(
        wrapper
    );

    try {

        hljs.highlightElement(
            code
        );

    } catch (e) {
    }
}


function renderMath(
    root
) {

    renderMathInElement(
        root,
        {
            delimiters: [
                {
                    left: "\\(",
                    right: "\\)",
                    display: false
                },
                {
                    left: "\\[",
                    right: "\\]",
                    display: true
                },
                {
                    left: "$$",
                    right: "$$",
                    display: true
                },
                {
                    left: "$",
                    right: "$",
                    display: false
                }
            ],

            throwOnError: false,

            ignoredTags: [
                "script",
                "noscript",
                "style",
                "textarea",
                "pre",
                "code"
            ]
        }
    );
}


function renderResponse(
    original
) {

    const content =
        document.getElementById(
            "content"
        );

    content.innerHTML = "";

    marked.setOptions({
        gfm: true,
        breaks: true
    });

    const parts =
        extractCodeBlocks(
            original
        );

    parts.forEach(
        function(part) {

            if (
                part.type ===
                "code"
            ) {

                appendCodePart(
                    content,
                    part.language,
                    part.code
                );

            } else {

                appendTextPart(
                    content,
                    part.value
                );
            }
        }
    );

    renderMath(content);
    fitChatMath(content);
}


function setResponse(
    encodedText
) {

    const binary =
        atob(
            encodedText
        );

    const bytes =
        Uint8Array.from(
            binary,
            c => c.charCodeAt(0)
        );

    const original =
        new TextDecoder(
            "utf-8"
        ).decode(bytes);

    renderResponse(
        original
    );
}


window.setResponse =
    setResponse;
