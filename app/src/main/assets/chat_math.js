const chatMathParts = [];

function protectMath(value) {

    const index =
        chatMathParts.length;

    chatMathParts.push(value);

    return (
        "MATHPLACEHOLDER" +
        index +
        "X"
    );
}


function prepareMathText(text) {

    chatMathParts.length = 0;

    let result = text;

    result =
        result.replace(
            /\\\[[\s\S]*?\\\]/g,
            function(match) {
                return protectMath(match);
            }
        );

    result =
        result.replace(
            /\\\([\s\S]*?\\\)/g,
            function(match) {
                return protectMath(match);
            }
        );

    result =
        result.replace(
            /\$\$[\s\S]*?\$\$/g,
            function(match) {
                return protectMath(match);
            }
        );

    result =
        result.replace(
            /\$[^$\n]+\$/g,
            function(match) {
                return protectMath(match);
            }
        );

    result =
        result.replace(
            /(?<![A-Za-z0-9_])([A-Za-z])\^\{([^}\n]+)\}/g,
            function(match) {

                return protectMath(
                    "\\(" +
                    match[1] +
                    "^{" +
                    match[2] +
                    "}" +
                    "\\)"
                );
            }
        );

    result =
        result.replace(
            /(?<![A-Za-z0-9_])([A-Za-z])\^([A-Za-z0-9]+)/g,
            function(match) {

                return protectMath(
                    "\\(" +
                    match[1] +
                    "^{" +
                    match[2] +
                    "}" +
                    "\\)"
                );
            }
        );

    result =
        result.replace(
            /(?<![A-Za-z0-9_])([A-Za-z])_([A-Za-z0-9]+)/g,
            function(match) {

                return protectMath(
                    "\\(" +
                    match[1] +
                    "_{" +
                    match[2] +
                    "}" +
                    "\\)"
                );
            }
        );

    result =
        result.replace(
            /(?<![A-Za-z0-9_])([A-Za-z]\s*\/\s*[A-Za-z0-9]+)(?![A-Za-z0-9_])/g,
            function(match) {

                return protectMath(
                    "\\(" +
                    match[1] +
                    "\\)"
                );
            }
        );

    return result;
}


function restoreMathText(
    container
) {

    const walker =
        document.createTreeWalker(
            container,
            NodeFilter.SHOW_TEXT
        );

    const nodes = [];

    let node;

    while (
        node =
            walker.nextNode()
    ) {

        nodes.push(node);
    }

    nodes.forEach(
        function(textNode) {

            let text =
                textNode.nodeValue;

            if (
                !text.includes(
                    "MATHPLACEHOLDER"
                )
            ) {
                return;
            }

            const fragment =
                document.createDocumentFragment();

            const pattern =
                /MATHPLACEHOLDER(\d+)X/g;

            let lastIndex = 0;
            let match;

            while (
                (match =
                    pattern.exec(text)) !== null
            ) {

                if (
                    match.index >
                    lastIndex
                ) {

                    fragment.appendChild(
                        document.createTextNode(
                            text.substring(
                                lastIndex,
                                match.index
                            )
                        )
                    );
                }

                const index =
                    Number(
                        match[1]
                    );

                const math =
                    chatMathParts[index];

                const isDisplay =
                    math.startsWith(
                        "\\["
                    ) ||
                    math.startsWith(
                        "$$"
                    );

                const element =
                    document.createElement(
                        isDisplay
                            ? "div"
                            : "span"
                    );

                element.className =
                    isDisplay
                        ? "chat-math-display"
                        : "chat-math-inline";

                element.textContent =
                    math;

                fragment.appendChild(
                    element
                );

                lastIndex =
                    pattern.lastIndex;
            }

            if (
                lastIndex <
                text.length
            ) {

                fragment.appendChild(
                    document.createTextNode(
                        text.substring(
                            lastIndex
                        )
                    )
                );
            }

            textNode.parentNode.replaceChild(
                fragment,
                textNode
            );
        }
    );
}
