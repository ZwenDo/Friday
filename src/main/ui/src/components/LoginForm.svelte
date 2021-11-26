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

<Form on:submit={onSubmit} title="Connection" submitButton="Log in">
    <FormField
        label="Username"
        name="username"
        type="text"
        placeholder="Username"
        bind:value="{username}"
    />
    <FormField
        label="Password"
        name="password"
        type="password"
        placeholder="Password"
        bind:value="{password}"
    />
</Form>
