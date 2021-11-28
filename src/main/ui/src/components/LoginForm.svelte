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
        name="username"
        type="text"
        placeholder="Username"
        required="true"
        bind:value="{username}"
    />
    <FormField
        label="Password"
        name="password"
        type="password"
        placeholder="Password"
        required="true"
        bind:value="{password}"
    />
</Form>
