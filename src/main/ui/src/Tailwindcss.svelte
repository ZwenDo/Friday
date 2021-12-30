<script>
    import 'tippy.js/dist/svg-arrow.css';
</script>
<style global>
    /* noinspection CssInvalidAtRule */
    @tailwind base;
    /* noinspection CssInvalidAtRule */
    @tailwind components;
    /* noinspection CssInvalidAtRule */
    @tailwind utilities;

    @layer base {
        .tippy-box[data-theme~='friday'] {
            @apply bg-purple-500 text-white px-2 py-[2px] rounded-lg;
        }

        .tippy-box[data-theme~='friday'] > .tippy-svg-arrow {
            @apply fill-current text-purple-500
        }

        .tippy-box[data-animation='friday'][data-placement^=top] {
            transform-origin: bottom
        }

        .tippy-box[data-animation='friday'][data-state=hidden] {
            transform: scale(0);
            opacity: .25
        }
    }

    @layer components {
        .input-field {
            @apply rounded-lg p-2 placeholder-transparent
            shadow-md border-gray-100 outline-none
            focus:ring-purple-200 focus:ring-2
            transition-all;

        }

        .full-field {
            -webkit-box-sizing: border-box;
            -moz-box-sizing: border-box;
            box-sizing: border-box;
            @apply w-full;
        }
    }

    @layer utilities {
        .clip-path-slant-up-right {
            clip-path: polygon(0% 0%, 100% 0%, 100% 10%, 0% 50%);
        }

        .bg-brand {
            @apply bg-gradient-to-r from-purple-900 via-purple-700 to-pink-400;
        }

        .bg-clip-text {
            background-clip: text;
            -webkit-background-clip: text;
        }
    }
</style>
