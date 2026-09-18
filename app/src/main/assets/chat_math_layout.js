function prepareChatMathLayout(
    root
) {

    const displays =
        root.querySelectorAll(
            ".chat-math-display"
        );

    displays.forEach(
        function(display) {

            const source =
                display.textContent.trim();

            if (
                source.length === 0
            ) {
                return;
            }

            display.dataset.mathSource =
                source;
        }
    );
}


function extractMathBody(
    source
) {

    const value =
        source.trim();

    if (
        value.startsWith("\\[") &&
        value.endsWith("\\]")
    ) {

        return {
            prefix: "\\[",
            suffix: "\\]",
            body: value.substring(
                2,
                value.length - 2
            )
        };
    }

    if (
        value.startsWith("$$") &&
        value.endsWith("$$")
    ) {

        return {
            prefix: "$$",
            suffix: "$$",
            body: value.substring(
                2,
                value.length - 2
            )
        };
    }

    return {
        prefix: "",
        suffix: "",
        body: value
    };
}


function splitMathSource(
    source
) {

    const math =
        extractMathBody(
            source
        );

    const body =
        math.body;

    const parts = [];

    let current = "";

    let parentheses = 0;
    let brackets = 0;
    let braces = 0;

    for (
        let i = 0;
        i < body.length;
        i++
    ) {

        const character =
            body.charAt(i);

        if (
            character === "("
        ) {
            parentheses++;
        }

        if (
            character === ")"
        ) {
            parentheses =
                Math.max(
                    0,
                    parentheses - 1
                );
        }

        if (
            character === "["
        ) {
            brackets++;
        }

        if (
            character === "]"
        ) {
            brackets =
                Math.max(
                    0,
                    brackets - 1
                );
        }

        if (
            character === "{"
        ) {
            braces++;
        }

        if (
            character === "}"
        ) {
            braces =
                Math.max(
                    0,
                    braces - 1
                );
        }

        const topLevel =
            parentheses === 0 &&
            brackets === 0 &&
            braces === 0;

        const previous =
            i > 0
                ? body.charAt(i - 1)
                : "";

        const next =
            i + 1 < body.length
                ? body.charAt(i + 1)
                : "";

        if (
            character === "=" &&
            topLevel &&
            previous !== "<" &&
            previous !== ">" &&
            previous !== "!" &&
            previous !== "=" &&
            next !== "="
        ) {

            if (
                current.trim().length > 0
            ) {

                parts.push(
                    current.trim()
                );
            }

            current =
                "= ";

            continue;
        }

        if (
            character === "," &&
            topLevel
        ) {

            current =
                current.trim() +
                ",";

            if (
                current.trim().length > 0
            ) {

                parts.push(
                    current.trim()
                );
            }

            current = "";

            continue;
        }

        current += character;
    }

    if (
        current.trim().length > 0
    ) {

        parts.push(
            current.trim()
        );
    }

    if (
        parts.length <= 1
    ) {

        return [
            source
        ];
    }

    return parts.map(
        function(part) {

            return (
                math.prefix +
                part +
                math.suffix
            );
        }
    );
}


window.prepareChatMathLayout =
    prepareChatMathLayout;

window.splitMathSource =
    splitMathSource;
