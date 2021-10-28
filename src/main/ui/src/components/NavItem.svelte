<script>
    //export let link;
    export let name;
    export let selected = false;
</script>

<button 
    class="
    duration-200 ease-in-out bg-gray-300
    px-3 pt-2 pb-4 rounded-t-xl shadow-2xl
    font-serif text-gray-500 text-4xl font-extralight"

    class:selected={selected}

    on:click={() => {selected = !selected}}>
    {name}
</button>

<style>
    .selected {
        @apply bg-white;
        @apply relative;
        @apply transform;
        @apply -translate-y-2;
    }
</style>