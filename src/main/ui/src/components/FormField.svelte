<script>
    export let label = null;
    export let name = label?.toLowerCase() ?? "";
    export let type = "text";
    export let required = false;
    export let value = "";

    function handleInputType(event) {
        event.target.type = type;
    }
</script>

<div class="form-field-container mt-9 mb-4 relative">
    <input
        class="peer
        input-field rounded-lg p-2 placeholder-transparent
        shadow-md border-1 border-gray-100 outline-none
        focus:ring-purple-200 focus:ring-2
        transition-all"
        on:input="{handleInputType}"
        id="{name}"
        {name}
        bind:value
        placeholder="{label}"
        {required}
    />
    <label
        class="
        transition-all
        absolute left-2 -top-6
        peer-placeholder-shown:font-normal peer-placeholder-shown:text-gray-400  peer-placeholder-shown:text-base peer-placeholder-shown:top-2
        peer-focus:font-semibold peer-focus:text-gray-600 peer-focus:text-sm peer-focus:-top-6
        font-semibold text-gray-600 text-sm"
        for="{name}"
    >
        {label}
    </label>
</div>