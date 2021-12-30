<script>
    import Fullcalendar from "svelte-fullcalendar";
    import dayGridPlugin from "@fullcalendar/daygrid";
    import rRulePlugin from "@fullcalendar/rrule";
    import {COOKIE_USER_ID, COOKIE_USER_TOKEN, getCookie} from "../utils/cookies";
    import {getContext, onMount} from "svelte";
    import {jsDateToFormDate} from "../utils/date";
    import listPlugin from "@fullcalendar/list";
    import EventDetails from "./EventDetails.svelte";

    export let calendarRefs = [];
    export let calendarRef = null;
    export let type = "dayGridMonth";

    const {open} = getContext('simple-modal');

    const options = {
        plugins: [dayGridPlugin, rRulePlugin, listPlugin],
        initialView: type,
        views: {
            dayGrid: {
                dayMaxEventRows: 5
            }
        },
        events: {
            url: "/api/event/allbyuser",
            method: "POST",
            extraParams: {
                userId: getCookie(COOKIE_USER_ID),
                token: getCookie(COOKIE_USER_TOKEN)
            }
        },
        eventClick(infos) {
           open(EventDetails, {calendarRefs, event: infos.event._def.extendedProps}, {
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
        },
        eventDataTransform(data) {
            cleanData(data);
            copyToExtendedProps(data);
            return data;
        }
    }

    function cleanData(data) {
        data.start[1]--;
        // set all day value
        if (data.end === undefined) {
            data.allDay = true;
        } else {
            data.allDay = false;
            data.end[1]--;
        }
    }

    function copyToExtendedProps(data) {
        // copy all the data to prevent discard
        data.extendedProps = {...data};
        data.extendedProps.extendedProps = null;
        data.extendedProps.start = jsDateToFormDate(new Date(...data.extendedProps.start));
        if (data.extendedProps.end !== undefined) {
            data.extendedProps.end = jsDateToFormDate(new Date(...data.extendedProps.end));
        }
    }

    onMount(() => calendarRefs.push(calendarRef));
</script>


<Fullcalendar bind:this={calendarRef} {options}/>
