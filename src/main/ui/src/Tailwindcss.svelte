<style global>
    /* noinspection CssInvalidAtRule */
    @tailwind base;
    /* noinspection CssInvalidAtRule */
    @tailwind components;
    /* noinspection CssInvalidAtRule */
    @tailwind utilities;

    @layer base {
        .clip-path-slant-up-right {
            clip-path: polygon(0% 0%, 100% 0%, 100% 10%, 0% 50%);
        }

        .bg-brand {
            @apply bg-gradient-to-r from-purple-900 via-purple-700 to-pink-400;
        }
    }
</style>
