<script>
    export let link;
    export let name;
    export let selected = false;
</script>

<button 
    class="
    {selected ? "bg-white relative transform -translate-y-2" : "bg-gray-200 font-extralight"} 
    duration-200 ease-in-out
    px-3 pt-2 pb-4 rounded-t-xl shadow-2xl
    font-serif  text-black text-2xl"
    href="{link}"
    on:click
>
    {name}
</button>