const chatMathParts = [];

const MATH_START = "\uE000";
const MATH_END = "\uE001";


function protectMath(value) {

    const index =
        chatMathParts.length;

    chatMathParts.push(value);

    return (
        MATH_START +
        index +
        MATH_END
    );
}


function protectExplicitMath(text) {

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

    return result;
}


function normalizeMathExpression(
    value
) {

    let result =
        value.trim();

    result =
        result.replace(
            /\^\s*\(([^()\r\n]+)\)/g,
            "^{$1}"
        );

    result =
        result.replace(
            /_\s*\(([^()\r\n]+)\)/g,
            "_{$1}"
        );

    result =
        result.replace(
            /\^\s*\{([^{}\r\n]+)\}/g,
            "^{$1}"
        );

    result =
        result.replace(
            /_\s*\{([^{}\r\n]+)\}/g,
            "_{$1}"
        );

    result =
        result.replace(
            /\^\s*([A-Za-z0-9]+)/g,
            "^{$1}"
        );

    result =
        result.replace(
            /_\s*([A-Za-z0-9]+)/g,
            "_{$1}"
        );

    result =
        result.replace(
            /\*/g,
            "\\times "
        );

    result =
        result.replace(
            /×/g,
            "\\times "
        );

    result =
        result.replace(
            /÷/g,
            "\\div "
        );

    result =
        result.replace(
            /≤/g,
            "\\le "
        );

    result =
        result.replace(
            /≥/g,
            "\\ge "
        );

    result =
        result.replace(
            /≠/g,
            "\\ne "
        );

    return result;
}


function isAllowedMathWord(
    word
) {

    const value =
        word.toLowerCase();

    if (
        /^[a-z]$/.test(value)
    ) {
        return true;
    }

    if (
        /^(sin|cos|tan|cot|sec|csc|log|ln|exp|sqrt|arcsin|arccos|arctan)$/.test(
            value
        )
    ) {
        return true;
    }

    if (
        /^(dy|dx|dt|du|dv|dw)$/.test(value)
    ) {
        return true;
    }

    if (
        /^d[0-9]*[a-z]$/.test(value)
    ) {
        return true;
    }

    return false;
}


function isValidMathExpression(
    value
) {

    const text =
        value.trim();

    if (
        text.length <= 1
    ) {
        return false;
    }

    if (
        /^[0-9]+$/.test(text)
    ) {
        return false;
    }

    if (
        /^[A-Za-z]$/.test(text)
    ) {
        return false;
    }

    const words =
        text.match(
            /[A-Za-z]+/g
        );

    if (
        !words
    ) {
        return false;
    }

    for (
        let i = 0;
        i < words.length;
        i++
    ) {

        if (
            !isAllowedMathWord(
                words[i]
            )
        ) {
            return false;
        }
    }

    if (
        /^[A-Za-z]+$/.test(text)
    ) {
        return false;
    }

    if (
        /^[A-Za-z]'{0,2}\s*\([^()\r\n]+\)$/.test(
            text
        )
    ) {
        return true;
    }

    if (
        /^[A-Za-z]'{0,2}\s*\([^()\r\n]+\)\s*=\s*.+$/.test(
            text
        )
    ) {
        return true;
    }

    if (
        /^\([^()\r\n]+\)$/.test(
            text
        )
    ) {
        return true;
    }

    if (
        /^\{[^{}\r\n]+\}$/.test(
            text
        )
    ) {
        return true;
    }

    if (
        /[=+\-*/^_]/.test(text)
    ) {
        return true;
    }

    if (
        /[×÷≤≥≠]/.test(text)
    ) {
        return true;
    }

    if (
        /\d+\s*[A-Za-z]/.test(text)
    ) {
        return true;
    }

    if (
        /[A-Za-z]\s*\d+/.test(text)
    ) {
        return true;
    }

    if (
        /[A-Za-z]\s*\(/.test(text)
    ) {
        return true;
    }

    return false;
}


function findMathCandidates(
    text
) {

    const candidates = [];

    const pattern =
        /(?<![A-Za-z0-9])((?:[+-]\s*)?(?:[A-Za-z]{1,3}'{0,2}\s*\([^()\r\n]+\)|[A-Za-z0-9]+)(?:\s*(?:\^|_|=|\+|\-|\*|\/|<|>|≤|≥|≠)\s*(?:[A-Za-z0-9]+|\([^()\r\n]+\)|\{[^{}\r\n]+\}))*)(?![A-Za-z0-9])/g;

    let match;

    while (
        (match =
            pattern.exec(text)) !== null
    ) {

        const value =
            match[1];

        if (
            value.length <= 1
        ) {
            continue;
        }

        if (
            !isValidMathExpression(
                value
            )
        ) {
            continue;
        }

        candidates.push({
            start:
                match.index +
                match[0].indexOf(value),

            end:
                match.index +
                match[0].indexOf(value) +
                value.length,

            value:
                value
        });
    }

    return candidates;
}


function protectMathCandidates(
    text
) {

    const candidates =
        findMathCandidates(
            text
        );

    if (
        candidates.length === 0
    ) {
        return text;
    }

    let result = text;

    for (
        let i =
            candidates.length - 1;
        i >= 0;
        i--
    ) {

        const candidate =
            candidates[i];

        const before =
            result.charAt(
                candidate.start - 1
            );

        const after =
            result.charAt(
                candidate.end
            );

        if (
            /[A-Za-z0-9]/.test(
                before
            ) ||
            /[A-Za-z0-9]/.test(
                after
            )
        ) {
            continue;
        }

        const normalized =
            normalizeMathExpression(
                candidate.value
            );

        result =
            result.substring(
                0,
                candidate.start
            ) +
            protectMath(
                "\\(" +
                normalized +
                "\\)"
            ) +
            result.substring(
                candidate.end
            );
    }

    return result;
}


function prepareMathText(
    text
) {

    chatMathParts.length = 0;

    let result =
        text;

    result =
        protectExplicitMath(
            result
        );

    result =
        protectMathCandidates(
            result
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
        (node =
            walker.nextNode()) !== null
    ) {

        nodes.push(node);
    }

    nodes.forEach(
        function(textNode) {

            const text =
                textNode.nodeValue;

            if (
                !text.includes(
                    MATH_START
                )
            ) {
                return;
            }

            const fragment =
                document.createDocumentFragment();

            const pattern =
                /\uE000(\d+)\uE001/g;

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

                if (
                    math === undefined
                ) {

                    fragment.appendChild(
                        document.createTextNode(
                            match[0]
                        )
                    );

                    lastIndex =
                        pattern.lastIndex;

                    continue;
                }

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
