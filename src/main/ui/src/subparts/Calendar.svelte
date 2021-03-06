<script>
    import {getContext, onMount} from "svelte";
    import {COOKIE_USER_ID, COOKIE_USER_TOKEN, getCookie} from "../utils/cookies";
    import {dateToYearDay, formDateToICalDate, jsDateToFormDate} from "../utils/date";
    import Fullcalendar from "svelte-fullcalendar";
    import EventDetails from "./EventDetails.svelte";
    import dayGridPlugin from "@fullcalendar/daygrid";
    import rRulePlugin from "@fullcalendar/rrule";
    import listPlugin from "@fullcalendar/list";

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
            open(EventDetails, {calendarRefs, closeable: true, event: infos.event._def.extendedProps}, {
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
        },
        eventDataTransform(data) {
            cleanData(data);
            copyToExtendedProps(data);
            return data;
        }
    };

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
        data.extendedProps.extendedProps = undefined;
        data.extendedProps.start = jsDateToFormDate(new Date(...data.extendedProps.start));
        if (data.extendedProps.end) {
            data.extendedProps.end = jsDateToFormDate(new Date(...data.extendedProps.end));
        }

        const rrule = data.rrule;
        const start = data.extendedProps.start;
        if (!rrule) return;
        if (
            rrule.includes('FREQ=YEARLY') &&
            !rrule.includes('BYMONTH=') &&
            !rrule.includes('BYMONTHDAY=') &&
            !rrule.includes('BYYEARDAY=') &&
            !rrule.includes('BYWEEKNO=') &&
            !rrule.includes('BYDAY=')
        ) {
            data.rrule += ';BYYEARDAY=' + (dateToYearDay(start) - 1);
            data.rrule += ';DTSTART=' + formDateToICalDate(start);
        }
    }

    onMount(() => calendarRefs.push(calendarRef));
</script>


<Fullcalendar bind:this={calendarRef} {options}/>
