<script>
    export let label = null;
    export let name = label?.toLowerCase() ?? "";
    export let type = "text";
    export let placeholder = label ?? "";
    export let required = false;
    export let value = "";

    function handleInputType(event) {
        event.target.type = type;
    }
</script>

<div class="form-field-container my-2">
    <label class="font-bold" for="{name}">{label}</label>
    <input
        class="
        input-field rounded-lg p-2
        shadow-md border-1 border-gray-100 outline-none
        focus:ring-purple-500 focus:ring-2
        transition duration-500 ease-in-out"
        on:input="{handleInputType}"
        id="{name}"
        {name}
        bind:value
        {placeholder}
        {required}
    />
</div>