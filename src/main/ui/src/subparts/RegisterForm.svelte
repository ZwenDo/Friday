<script>
    import FormField from "../components/FormField.svelte";
    import Form from "../components/Form.svelte";
    import {registerUser} from "../stores/user_store";

    export let extendClass = "";

    let regUsername = "";
    let regPassword = "";
    let regPasswordR = "";

    function onSubmit() {
        if (regPassword !== regPasswordR) {
            alert("Passwords do not match");
            return;
        }
        registerUser(regUsername, regPassword, _ =>{
            alert("Username already exists");
            regUsername = "";
            regPassword = "";
            regPasswordR = "";
        });
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
