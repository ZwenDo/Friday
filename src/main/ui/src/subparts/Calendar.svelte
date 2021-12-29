<script>
    import Fullcalendar from "svelte-fullcalendar";
    import dayGridPlugin from "@fullcalendar/daygrid";
    import rRulePlugin from "@fullcalendar/rrule";
    import {COOKIE_USER_ID, COOKIE_USER_TOKEN, getCookie} from "../utils/cookies";

    export let calendarRef = null;

    const options = {
        plugins: [dayGridPlugin, rRulePlugin],
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
            if (data.end === undefined) { // if no end set all day
                data.allDay = true;
            } else {
                data.end[1]--;
            }
            return data;
        }
    }
</script>


<Fullcalendar bind:this={calendarRef} {options}/>
