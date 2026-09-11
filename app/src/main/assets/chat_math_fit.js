function fitChatMath(root) {

    const equations =
        root.querySelectorAll(
            ".chat-math-display"
        );

    equations.forEach(
        function(equation) {

            const katex =
                equation.querySelector(
                    ".katex"
                );

            if (!katex) {
                return;
            }

            katex.style.display =
                "inline-block";

            katex.style.whiteSpace =
                "nowrap";

            const availableWidth =
                equation.clientWidth;

            if (
                availableWidth <= 0
            ) {
                return;
            }

            let currentSize =
                parseFloat(
                    window.getComputedStyle(
                        katex
                    ).fontSize
                );

            let actualWidth =
                katex.getBoundingClientRect()
                    .width;

            if (
                actualWidth <=
                availableWidth
            ) {
                return;
            }

            let scale =
                availableWidth /
                actualWidth;

            let newSize =
                currentSize *
                scale;

            const minimumSize =
                10;

            if (
                newSize <
                minimumSize
            ) {
                newSize =
                    minimumSize;
            }

            katex.style.fontSize =
                newSize + "px";

            actualWidth =
                katex.getBoundingClientRect()
                    .width;

            if (
                actualWidth >
                availableWidth
            ) {

                scale =
                    availableWidth /
                    actualWidth;

                newSize =
                    newSize *
                    scale;

                if (
                    newSize <
                    minimumSize
                ) {
                    newSize =
                        minimumSize;
                }

                katex.style.fontSize =
                    newSize + "px";
            }
        }
    );
}


window.fitChatMath =
    fitChatMath;
