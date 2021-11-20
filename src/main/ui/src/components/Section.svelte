<script>
    export let title;
    export let subtitle = null;
</script>

<section
        class="
    font-serif
    p-5 sm:p-10 my-5
    m-auto w-full
    rounded-lg shadow-inner shadow-2xl"
>
    <h2 class="font-extrabold text-2xl">{title}</h2>
    {#if subtitle != null}
        <h3>{subtitle}</h3>
    {/if}
    <slot></slot>
</section>