<script>
    import {getContext, onMount} from "svelte";
    import {push, replace} from "svelte-spa-router";
    import {COOKIE_USER_NAME, COOKIE_USER_TOKEN, deleteCookies, getCookie} from "../utils/cookies";
    import {isLoginValid, logoutUser} from "../stores/login_store";
    import Button from "../components/Button.svelte";
    import EventForm from "../subparts/EventForm.svelte";
    import ImportForm from "../subparts/ImportForm.svelte";
    import Heading from "../components/Heading.svelte";
    import Section from "../components/Section.svelte";
    import Calendar from "../subparts/Calendar.svelte";
    import {nextEvent} from "../stores/event_store";
    import EventDetails from "../subparts/EventDetails.svelte";
    import {jsDateToFormDate} from "../utils/date";

    const {open} = getContext('simple-modal');

    let calendarRefs = [];

    let next = null;

    function setNext(n) {
        next = n;
    }

    export function fetchNextEvent() {
        nextEvent(d => {
            d.start = jsDateToFormDate(new Date(...d.start));
            if (d.end) {
                d.end = jsDateToFormDate(new Date(...d.end));
            } else {
                delete d.end;
                d.allDay = true;
            }
            setNext(d);
        });
    }

    onMount(() => {
        if (!getCookie(COOKIE_USER_NAME) || !getCookie(COOKIE_USER_TOKEN)) {
            replace("/login");
            return;
        }
        isLoginValid(() => {
            deleteCookies();
            replace("/login");
        });
        fetchNextEvent();
    });


    function showImportForm() {
        open(ImportForm, {calendarRefs}, {
            closeButton: false,
            styleWindow: {
                backgroundColor: '#ffffff',
                overflow: 'hidden',
            },
            styleContent: {
                display: 'flex',
                justifyContent: 'center',
                overflowY: 'scroll',
            }
        });
    }

    function showEventForm() {
        open(EventForm, {calendarRefs}, {
            closeButton: false,
            styleWindow: {
                backgroundColor: '#ffffff',
                overflow: 'hidden',
            },
            styleContent: {
                display: 'flex',
                justifyContent: 'center',
                overflowY: 'scroll',
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
        <div class="flex flex-col-reverse sm:flex-row mt-2 sm:mt-0">
            <Button
                on:click={showImportForm}
            >
                Import Events
            </Button>
            <Button
                extendClass="sm:ml-4"
                on:click={showEventForm}
            >
                Create Event
            </Button>
            <Button
                extendClass="bg-pink-500 hover:bg-pink-700 sm:ml-4"
                on:click={logout}
            >
                Logout
            </Button>
        </div>
    </div>
    <Section title="Next Event">
        {#if next}
            <EventDetails {calendarRefs} event={next}/>
        {:else}
            <p class="m-7 text-lg font-thin">
                No next event.
            </p>
        {/if}
    </Section>
    <Section title="Today">
        <Calendar bind:calendarRefs={calendarRefs} type="listDay"/>
    </Section>
    <Section title="This month">
        <Calendar bind:calendarRefs={calendarRefs}/>
    </Section>
</div>
