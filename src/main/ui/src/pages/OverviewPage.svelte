<script>
    import {getContext, onMount} from "svelte";
    import {isLoginValid, logoutUser} from "../stores/login_store";
    import {COOKIE_USER_NAME, COOKIE_USER_TOKEN, deleteCookies, getCookie} from "../utils/cookies";
    import Button from "../components/Button.svelte";
    import EventForm from "../subparts/EventForm.svelte";
    import Heading from "../components/Heading.svelte";
    import Section from "../components/Section.svelte";
    import {push, replace} from "svelte-spa-router";
    import Calendar from "../subparts/Calendar.svelte";

    let calendarRef;

    const {open} = getContext('simple-modal');

    onMount(() => {
        if (!getCookie(COOKIE_USER_NAME) || !getCookie(COOKIE_USER_TOKEN)) {
            replace("/login");
            return;
        }
        isLoginValid(() => {
            deleteCookies();
            replace("/login");
        });
    });

    function showEventForm() {
        open(EventForm, { calendarRef }, {
            styleWindow: {
                width: '90vw',
                height: '90vh',
                backgroundColor: '#ffffff',
            },
            styleContent: {
                width: '100%',
                height: '100%',
            }
        });
    }

    function logout() {
        logoutUser(_ => push("/login"));
    }
</script>

<div class="p-4">
    <div class="sm:flex justify-between">
        <div>
            <Heading>Hello, {getCookie(COOKIE_USER_NAME)}!</Heading>
        </div>
        <div>
            <Button
                on:click={showEventForm}
            >
                Create Event
            </Button>
            <Button
                extendClass="bg-pink-500 hover:bg-pink-700 ml-4"
                on:click={logout}
            >
                Logout
            </Button>
        </div>
    </div>
    <Section title="Next">
        BBBBBBBBH
    </Section>
    <Section title="Today">
        AAAAAAAAH
    </Section>
    <Section title="This month">
        <Calendar bind:calendarRef/>
    </Section>
</div>
