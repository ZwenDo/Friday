<script>
    import FormField from "./FormField.svelte";
    import Form from "./Form.svelte";

    let username = "";
    let password = "";

    function onSubmit() {
        // TODO: Validate form
        console.log(username, password);
    }
</script>

<Form on:submit={onSubmit} title="Connection" submitText="Log in">
    <FormField
        label="Username"
        type="text"
        required="true"
        bind:value="{username}"
    />
    <FormField
        label="Password"
        type="password"
        required="true"
        bind:value="{password}"
    />
</Form>
