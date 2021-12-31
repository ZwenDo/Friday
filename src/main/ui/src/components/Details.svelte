<script>
    import {slide} from 'svelte/transition';

    export let name = 'Details';
    export let open = false;
    export let extendClass = '';

    let attrs;
    $: {
        const {name, open, ...other} = $$props;
        delete other.class;
        attrs = other;
    }
    $: active = open;

    function onclick(e) {
        open = !open;
        e.target.classList.remove('focus-visible');
    }
</script>

<div
    {...attrs}
    class="panel w-full
    relative box-border bg-white shadow-md rounded-lg
    border-gray-100 border-1 transition-all duration-300
    first:border-t-0
    {extendClass}" class:active
>
    <button
        class="header
        flex items-start w-full mh48
        cursor-pointer bg-none text-base text-left
        leading-none border-1 outline-none
        m-0 transition-all duration-300"
        on:click={onclick} type="button"
    >
        <span class="flex-1 span">{name}</span>
        <slot name="icon">
            <i class="icon inline-block">
                <svg fill="currentColor" height="24" viewBox="0 0 24 24" width="24">
                    <path d="M16.59 8.59L12 13.17 7.41 8.59 6 10l6 6 6-6z"/>
                </svg>
            </i>
        </slot>
    </button>

    {#if active}
        <div class="m-0 px-4" transition:slide|local={{ duration: 250 }}>
            <slot/>
        </div>
    {/if}
</div>

<style>
    .header {
        padding: 10px 22px;
    }

    .panel:first-child::before {
        @apply hidden;
    }

    .panel.active::before {
        @apply hidden;
    }

    .panel :global(.icon) {
        @apply transition-all duration-300;
    }

    .active .header {
        @apply min-h-[64px];
    }

    .active :global(.icon) {
        @apply rotate-180;
    }

    .mh48 {
        min-height: 48px;
    }

    .span {
        line-height: 24px;
    }

    .icon {
        line-height: 0.5;
    }
</style>
