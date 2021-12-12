<script>
    import LoginForm from "../subparts/LoginForm.svelte";
    import RegisterForm from "../subparts/RegisterForm.svelte";
</script>

<div class="h-full w-full flex items-center justify-between">
    <LoginForm/>
    <div class="hidden h-2/3 border rounded-full sm:block"></div>
    <RegisterForm/>
</div>