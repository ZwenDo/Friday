<script>
    import LoginForm from "../subparts/LoginForm.svelte";
    import RegisterForm from "../subparts/RegisterForm.svelte";
    import Button from "../components/Button.svelte";
    import {onMount} from "svelte";
    import {replace} from "svelte-spa-router";
    import {COOKIE_USER_ID, COOKIE_USER_TOKEN, getCookie} from "../utils/cookies";

    let buttonText;
    let toggle = false;

    onMount(_ => {
        if (getCookie(COOKIE_USER_ID) && getCookie(COOKIE_USER_TOKEN)) {
            replace("/");
        }
    });

    $: if (!toggle) {
        buttonText = "Go to connection";
    } else {
        buttonText = "Go to registration";
    }

    function toggleSignUp() {
        toggle = !toggle;
    }
</script>

<div class="h-full w-full flex flex-col sm:flex-row items-center justify-between">
    <div class="sm:hidden float-right w-full mt-3">
        <Button extendClass="sm:hidden w-30" on:click={toggleSignUp}>{buttonText}</Button>
    </div>
    <LoginForm extendClass="{toggle ? 'flex' : 'hidden'} sm:flex justify-center items-center"/>
    <div class="hidden h-2/3 border rounded-full sm:block"></div>
    <RegisterForm extendClass="{!toggle ? 'flex' : 'hidden'} sm:flex justify-center items-center"/>
</div>
