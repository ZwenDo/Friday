<script>
    export let label = null;
    export let checked = false;
    export let name = label?.toLowerCase() ?? "";
</script>

<label class="flex justify-start items-center ml-2" for="{name}">
    <div
        class="
        bg-white border-2 rounded border-gray-400
        w-6 h-6 mr-2
        flex flex-shrink-0 justify-center items-center
        focus-within:ring-purple-200 focus-within:ring-2"
    >
        <input bind:checked class="opacity-0 absolute" id="{name}" {name} type="checkbox">
        <svg class="fill-current hidden w-4 h-4 text-purple-500 pointer-events-none" viewBox="0 0 20 20">
            <path d="M0 11l2-2 5 5L18 3l2 2L7 18z"/>
        </svg>
    </div>
    <div class="select-none font-semibold text-gray-600 text-sm">{label}</div>
</label>

<style>
    input:checked + svg {
        @apply block
    }
</style>