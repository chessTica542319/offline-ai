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
        math.body.trim();

    if (
        body.length === 0
    ) {
        return [
            source
        ];
    }

    /*
     * --------------------------------------------------
     * STEP 1
     * Respect explicit LaTeX line breaks first.
     *
     * Example:
     *
     * du = e^x \\
     * dx = sin x
     *
     * becomes two natural math lines.
     * --------------------------------------------------
     */

    const explicitLines =
        body
            .split(
                /\\\\/g
            )
            .map(
                function(line) {
                    return line.trim();
                }
            )
            .filter(
                function(line) {
                    return line.length > 0;
                }
            );

    if (
        explicitLines.length > 1
    ) {

        return explicitLines.map(
            function(line) {

                return (
                    math.prefix +
                    line +
                    math.suffix
                );
            }
        );
    }


    /*
     * --------------------------------------------------
     * STEP 2
     * Find natural separators.
     *
     * We DO NOT split immediately.
     * We first build semantic chunks.
     * --------------------------------------------------
     */

    const chunks = [];

    let current = "";

    let parentheses = 0;
    let brackets = 0;
    let braces = 0;

    function pushCurrent() {

        const value =
            current.trim();

        if (
            value.length > 0
        ) {

            chunks.push(
                value
            );
        }

        current = "";
    }


    for (
        let i = 0;
        i < body.length;
        i++
    ) {

        const character =
            body.charAt(i);


        /*
         * Track grouping.
         */

        if (
            character === "("
        ) {
            parentheses++;
        }

        else if (
            character === ")"
        ) {

            parentheses =
                Math.max(
                    0,
                    parentheses - 1
                );
        }

        else if (
            character === "["
        ) {
            brackets++;
        }

        else if (
            character === "]"
        ) {

            brackets =
                Math.max(
                    0,
                    brackets - 1
                );
        }

        else if (
            character === "{"
        ) {
            braces++;
        }

        else if (
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


        /*
         * ------------------------------------------------
         * "="
         *
         * Keep "=" with the RIGHT side.
         *
         * Example:
         *
         * du = e^x
         *
         * becomes one chunk:
         *
         * du = e^x
         *
         * NOT:
         *
         * du
         * = e^x
         * ------------------------------------------------
         */

        if (
            character === "=" &&
            topLevel
        ) {

            current =
                current.trim() +
                " =";

            /*
             * Do not push yet.
             *
             * Continue collecting the RHS.
             */

            continue;
        }


        /*
         * ------------------------------------------------
         * Comma
         *
         * Commas are natural boundaries.
         * ------------------------------------------------
         */

        if (
            character === "," &&
            topLevel
        ) {

            current =
                current.trim() +
                ",";

            pushCurrent();

            continue;
        }


        /*
         * ------------------------------------------------
         * Natural-language separators.
         *
         * We only split when these words appear at the
         * top level.
         * ------------------------------------------------
         */

        if (
            topLevel &&
            (
                body.substring(
                    i,
                    i + 5
                ).toLowerCase() ===
                " and "
                ||
                body.substring(
                    i,
                    i + 4
                ).toLowerCase() ===
                " or "
                ||
                body.substring(
                    i,
                    i + 10
                ).toLowerCase() ===
                " therefore "
            )
        ) {

            /*
             * Keep the separator with the next chunk.
             */

            pushCurrent();

            if (
                body.substring(
                    i,
                    i + 5
                ).toLowerCase() ===
                " and "
            ) {

                current =
                    "and ";

                i += 4;

                continue;
            }

            if (
                body.substring(
                    i,
                    i + 4
                ).toLowerCase() ===
                " or "
            ) {

                current =
                    "or ";

                i += 3;

                continue;
            }

            current =
                "therefore ";

            i += 9;

            continue;
        }


        current +=
            character;
    }


    pushCurrent();


    /*
     * --------------------------------------------------
     * If there was no useful split, return original.
     * --------------------------------------------------
     */

    if (
        chunks.length <= 1
    ) {

        return [
            source
        ];
    }


    /*
     * --------------------------------------------------
     * STEP 3
     *
     * Merge tiny/orphan chunks.
     *
     * This prevents:
     *
     * du =
     * e^x
     *
     * or:
     *
     * dx
     * and
     * v = ...
     * --------------------------------------------------
     */

    const merged = [];

    chunks.forEach(
        function(chunk) {

            const value =
                chunk.trim();

            if (
                value.length === 0
            ) {
                return;
            }

            /*
             * If a chunk is only an operator,
             * attach it to the previous chunk.
             */

            if (
                /^[=+\-*/<>≤≥≠]+$/.test(
                    value
                )
            ) {

                if (
                    merged.length > 0
                ) {

                    merged[
                        merged.length - 1
                    ] +=
                        " " +
                        value;
                }

                return;
            }

            merged.push(
                value
            );
        }
    );


    /*
     * --------------------------------------------------
     * STEP 4
     * Rebuild KaTeX expressions.
     * --------------------------------------------------
     */

    return merged.map(
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
