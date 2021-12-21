<script>
    import FormField from "../components/FormField.svelte";
    import Form from "../components/Form.svelte";

    export let extendClass = "";

    let regUsername = "";
    let regPassword = "";
    let regPasswordR = "";

    function onSubmit() {
        // TODO: Validate form
    }
</script>

<Form {extendClass} on:submit={onSubmit} submitText="Sign up" title="Register">
    <FormField
        label="Username"
        name="regUsername"
        type="text"
        required="true"
        bind:value="{regUsername}"
    />
    <FormField
        label="Password"
        name="regPassword"
        type="password"
        required="true"
        bind:value="{regPassword}"
    />
    <FormField
        label="Repeat password"
        name="regPasswordR"
        type="password"
        required="true"
        bind:value="{regPasswordR}"
    />
</Form>