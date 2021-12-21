<script>
    export let type = "button";
    export let disabled = false;
</script>

<button
    {type}
    {disabled}
    class="
    text-xl text-white px-4 py-2 my-2
    bg-purple-500 rounded-full
    transform hover:bg-purple-600 hover:scale-110
    transition-all"
>
    <slot/>
</button>