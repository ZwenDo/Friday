<script>
    import FormField from "../components/FormField.svelte";
    import Form from "../components/Form.svelte";
    import {loginUser} from "../stores/login_store";
    import {push} from "svelte-spa-router";

    export let extendClass = "";

    let logUsername = "";
    let logPassword = "";

    function onSubmit() {
        loginUser(logUsername, logPassword, _ => {
            alert("Invalid username or password");
            logUsername = "";
            logPassword = "";
        },
        _ => push("/"));
    }
</script>

<Form {extendClass} on:submit={onSubmit} submitText="Log in" title="Connection">
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
