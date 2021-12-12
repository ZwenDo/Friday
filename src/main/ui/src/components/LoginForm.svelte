<script>
    import FormField from "./FormField.svelte";
    import Form from "./Form.svelte";

    let logUsername = "";
    let logPassword = "";

    function onSubmit() {
        // TODO: Validate form
    }
</script>

<Form on:submit={onSubmit} title="Connection" submitText="Log in">
    <FormField
        label="Username"
        name="logUsername"
        type="text"
        required="true"
        bind:value="{logUsername}"
    />
    <FormField
        label="Password"
        name="logPassword"
        type="password"
        required="true"
        bind:value="{logPassword}"
    />
</Form>
