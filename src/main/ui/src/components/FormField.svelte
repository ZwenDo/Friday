<script>
    export let value = "";
    export let label = null;
    export let name = (label ?? "").toLowerCase();
    export let type = "text";
    export let placeholder = null;

    function handleInputType(event) {
        event.target.type = type;
    }
</script>

<div class="form-field-container">
    <label class="font-bold" for="{name}">{label}</label>
    <input
        on:input="{handleInputType}"
        id="{name}"
        {name}
        bind:value
        {placeholder}
    />
</div>