<script>
    export let title = null;
    export let submitButton = null;
</script>

<div class="
    main-container font-serif
    w-full h-full flex flex-auto items-center justify-center"
>
    <div class="form-container">
        <h2 class="font-extrabold text-4xl">{title}</h2>
        <form on:submit|preventDefault>
            <slot></slot>
            <button type="submit">{submitButton}</button>
        </form>
    </div>
</div>