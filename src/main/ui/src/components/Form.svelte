<script>
    import Button from "./Button.svelte";

    export let title = null;
    export let submitText = null;
</script>

<div class="
    main-container font-serif
    w-full h-full flex flex-auto items-center justify-center"
>
    <div class="form-container">
        <h2 class="font-extrabold text-4xl">{title}</h2>
        <form on:submit|preventDefault>
            <slot></slot>
            <Button type="submit">{submitText}</Button>
        </form>
    </div>
</div>