<script>
    import Fullcalendar from "svelte-fullcalendar";
    import dayGridPlugin from "@fullcalendar/daygrid";
    import iCalendarPlugin from "@fullcalendar/icalendar";
    import rRulePlugin from "@fullcalendar/rrule";
    import {COOKIE_USER_ID, COOKIE_USER_TOKEN, getCookie} from "../utils/cookies";

    let calendarRef = null;

    const options = {
        plugins: [dayGridPlugin, iCalendarPlugin, rRulePlugin],
        events: {
            url: "/api/event/allbyuser",
            method: "POST",
            extraParams: {
                userId: getCookie(COOKIE_USER_ID),
                token: getCookie(COOKIE_USER_TOKEN)
            }
        },
        eventClick(infos) {
            // TODO show detailed infos popup
        },
        eventDataTransform(data) {
            data.start[1]--;
            data.end[1]--;
            console.log(data);
            return data;
        }
    }
</script>


<Fullcalendar bind:this={calendarRef} {options}/>
