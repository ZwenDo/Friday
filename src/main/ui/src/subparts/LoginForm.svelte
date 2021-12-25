<script>
    import FormField from "../components/FormField.svelte";
    import Form from "../components/Form.svelte";
    import {loginUser} from "../stores/login_store";

    export let extendClass = "";

    let logUsername = "";
    let logPassword = "";

    function onSubmit() {
        if (!loginUser(logUsername, logPassword)) {
            alert("Invalid username or password");
            logUsername = "";
            logPassword = "";
        }
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