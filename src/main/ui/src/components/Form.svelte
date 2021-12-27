<script>
    import Button from "./Button.svelte";
    import Heading from "./Heading.svelte";

    export let extendClass = "";

    export let title = null;
    export let submitText;
</script>

<div class="
    main-container font-serif
    w-full h-full
    flex flex-auto items-center justify-center
    {extendClass}"
>
    <div>
        {#if title}
            <Heading>{title}</Heading>
        {/if}
        <hr class="w-full mt-4"/>
        <form on:submit|preventDefault>
            <slot/>
            <Button type="submit">{submitText}</Button>
        </form>
    </div>
</div>