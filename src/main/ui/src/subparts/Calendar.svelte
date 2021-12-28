<script>
    import Fullcalendar from "svelte-fullcalendar";
    import dayGridPlugin from "@fullcalendar/daygrid";
    import iCalendarPlugin from "@fullcalendar/icalendar";
    import {COOKIE_USER_ID, COOKIE_USER_TOKEN, getCookie} from "../utils/cookies";

    export let calendarRef;

    const options = {
        plugins: [dayGridPlugin, iCalendarPlugin],
        events: {
            url: "/api/event/" + "allbyuser/" + getCookie(COOKIE_USER_ID) + "/" + getCookie(COOKIE_USER_TOKEN),
            method: 'GET',
            format: 'ics'
        },
        eventClick(infos) {
            console.log(infos);
        }
    }

</script>


<Fullcalendar bind:this={calendarRef} {options}/>
