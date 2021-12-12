<script>
    import FormField from "./FormField.svelte";
    import Form from "./Form.svelte";

    let regUsername = "";
    let regPassword = "";
    let regPasswordR = "";

    function onSubmit() {
        // TODO: Validate form
    }
</script>

<Form on:submit={onSubmit} title="Register" submitText="Sign up">
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