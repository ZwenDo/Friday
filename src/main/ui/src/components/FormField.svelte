<script>
    import {onMount} from "svelte";
    import {tooltip} from "../utils/tooltip";

    export let label = null;
    export let name = label?.toLowerCase() ?? "";
    export let type = "text";
    export let required = false;
    export let disabled = false;
    export let value = "";
    export let min = null;
    export let max = null;
    export let pattern = null;
    export let hint = null;
    export let extendClass = "";
    export let fieldClass = "";

    let field;

    onMount(() => {
        if (!field) return;
        if (type === "float") {
            field.type = "number";
            field.step = "0.01";
        } else {
            field.type = type;
        }
    });
</script>

<div class="mt-9 mb-4 relative {extendClass}">
    {#if type.startsWith("textarea")}
        <textarea
            class="input-field peer {disabled ? 'opacity-75' : ''} {fieldClass}"
            id="{name}"
            {name}
            bind:value
            cols="{type.split(' ')[1] ?? '40'}"
            rows="{type.split(' ')[2] ?? '10'}"
            placeholder="{label}"
            {required}
            {disabled}
            style="resize: none;"
            use:tooltip title="{hint}"
        ></textarea>
    {:else}
        <input
            class="input-field peer {disabled ? 'opacity-75' : ''} {fieldClass}"
            on:change
            bind:this={field}
            id="{name}"
            {name}
            bind:value
            placeholder="{label}"
            {required}
            {disabled}
            {min}
            {max}
            {pattern}
            use:tooltip title="{hint}"
        />
    {/if}
    {#if label}
        <label
            class="
            transition-all
            absolute left-2 -top-6
            peer-placeholder-shown:font-normal peer-placeholder-shown:text-gray-400  peer-placeholder-shown:text-base peer-placeholder-shown:top-2
            peer-focus:font-semibold peer-focus:text-gray-600 peer-focus:text-sm peer-focus:-top-6
            font-semibold text-gray-600 text-sm
            {disabled ? 'opacity-25 peer-focus:opacity-25 peer-placeholder-shown:opacity-25' : ''}"
            for="{name}"
        >
            {label}
        </label>
    {/if}
</div>
