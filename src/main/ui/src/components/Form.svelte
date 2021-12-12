<script>
    import Button from "./Button.svelte";
    import Heading from "./Heading.svelte";

    export let title = null;
    export let submitText = null;
</script>

<div class="
    main-container font-serif
    w-full h-full
    flex flex-auto items-center justify-center"
>
    <div>
        <Heading>{title}</Heading>
        <hr class="w-full mt-4"/>
        <form on:submit|preventDefault>
            <slot/>
            <Button type="submit">{submitText}</Button>
        </form>
    </div>
</div>