<script>
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
            <button
                type="submit"
                class="
                submit-button text-xl text-white px-4 py-2 my-2
                bg-purple-500 rounded-full
                transform hover:bg-purple-600 hover:scale-110
                transition-all"
            >
                {submitText}
            </button>
        </form>
    </div>
</div>