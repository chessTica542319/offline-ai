function getMathAvailableWidth(
    element
) {

    const styles =
        window.getComputedStyle(
            element
        );

    const paddingLeft =
        parseFloat(
            styles.paddingLeft
        ) || 0;

    const paddingRight =
        parseFloat(
            styles.paddingRight
        ) || 0;

    return Math.max(
        1,
        element.clientWidth -
        paddingLeft -
        paddingRight -
        2
    );
}


function getMathElementWidth(
    element
) {

    const katex =
        element.querySelector(
            ".katex"
        );

    if (!katex) {
        return 0;
    }

    return katex
        .getBoundingClientRect()
        .width;
}


function shrinkMathElement(
    element,
    minimumSize
) {

    const katex =
        element.querySelector(
            ".katex"
        );

    if (!katex) {
        return true;
    }

    const availableWidth =
        getMathAvailableWidth(
            element
        );

    // Math font size: change 16 and 14 here when testing.
    const maximumSize = 18;
    const minimumMathSize = 16;

    let currentSize =
        maximumSize;

    katex.style.fontSize =
        currentSize + "px";

    let actualWidth =
        getMathElementWidth(
            element
        );

    if (
        actualWidth <=
        availableWidth
    ) {

        return true;
    }

    while (
        currentSize >
        minimumMathSize
    ) {

        currentSize =
            currentSize - 1;

        katex.style.fontSize =
            currentSize + "px";

        actualWidth =
            getMathElementWidth(
                element
            );

        if (
            actualWidth <=
            availableWidth
        ) {

            return true;
        }
    }

    return (
        getMathElementWidth(
            element
        ) <=
        availableWidth
    );
}


function renderMathPart(
    element
) {

    renderMathInElement(
        element,
        {
            delimiters: [
                {
                    left: "\\[",
                    right: "\\]",
                    display: true
                },
                {
                    left: "$$",
                    right: "$$",
                    display: true
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


function createMathLine(
    source
) {

    const line =
        document.createElement(
            "div"
        );

    line.className =
        "chat-math-line";

    line.dataset.mathSource =
        source;

    line.textContent =
        source;

    return line;
}


function splitDisplayMath(
    display,
    source
) {

    const parts =
        splitMathSource(
            source
        );

    if (
        parts.length <= 1
    ) {

        return false;
    }

    display.innerHTML = "";

    parts.forEach(
        function(part) {

            const line =
                createMathLine(
                    part
                );

            display.appendChild(
                line
            );

            renderMathPart(
                line
            );
        }
    );

    return true;
}


function fitMathLine(
    line
) {

    return shrinkMathElement(
        line,
        14
    );
}


function fitChatMath(
    root
) {

    const displays =
        root.querySelectorAll(
            ".chat-math-display"
        );

    displays.forEach(
        function(display) {

            const source =
                display.dataset.mathSource;

            if (
                !source
            ) {
                return;
            }

            const fits =
                shrinkMathElement(
                    display,
                    14
                );

            if (
                fits
            ) {
                return;
            }

            const split =
                splitDisplayMath(
                    display,
                    source
                );

            if (
                !split
            ) {

                return;
            }

            const lines =
                display.querySelectorAll(
                    ".chat-math-line"
                );

            lines.forEach(
                function(line) {

                    fitMathLine(
                        line
                    );
                }
            );
        }
    );
}


window.fitChatMath =
    fitChatMath;
