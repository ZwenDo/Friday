<script>
    import Form from "../components/Form.svelte";
    import FormField from "../components/FormField.svelte";
    import {getContext} from "svelte";
    import Button from "../components/Button.svelte";
    import {createRrule} from "../utils/rrule";
    import {createEvent} from "../stores/event_store";

    const {close} = getContext('simple-modal');

    export let calendarRef;

    function today() {
        const now = new Date();
        const minutes = now.getMinutes() < 10 ? "0" + now.getMinutes() : now.getMinutes();
        return now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate() + "T" + now.getHours() + ":" + minutes;
    }

    let event = {
        title: undefined,
        description: null,
        place: null,
        startDate: today(),
        endDate: today(),
        allDay: false,
        latitude: null,
        longitude: null,
        recurRuleParts: null,
    };

    let hasRecurrenceChecked = false;

    let rec = {
        freq: "WEEKLY",
        byMonth: "",
        byYearDay: "",
        byWeekNo: "",
        byDay: "",
        byMonthDay: "",
        bySetPos: ""
    };

    function createNewEvent() {
        if (hasRecurrenceChecked) {
            const rrule = createRrule(rec);
            console.log(rrule);
            event.recurRuleParts = rrule;
        }
        createEvent(event, _ => {
                calendarRef.getAPI().refetchEvents();
                close();
            });

    }
</script>

<div class="w-full h-full flex justify-center items-center">
    <Form on:submit={createNewEvent} submitText="Create" title="Create an event">
        <div class="sm:flex">
            <div class="sm:pr-4">
                <FormField bind:value={event.title} label="Title*" name="title" required="true" type="text"/>
                <FormField bind:value={event.description} label="Description" name="description" type="textarea 18 5"/>
            </div>
            <div>
                <FormField bind:value={event.place} label="Place" name="place" type="text"/>
                <FormField bind:value={event.startDate} label="Start Date*" name="startDate" required="true"
                           type="datetime-local"/>
                <FormField disabled="{event.allDay}" bind:value={event.endDate} label="End Date*" name="endDate"
                           required="true" type="datetime-local"/>
                <div class="form-field-container relative left-2">
                    <label class="font-semibold text-gray-600 text-sm" for="allDay">All Day</label>
                    <input bind:checked={event.allDay} class="input-field" id="allDay" name="allDay" type="checkbox"/>
                </div>
            </div>
        </div>
        <details class="my-4 transition-all">
            <summary class="font-semibold text-gray-600 text-sm transition-all">Localisation</summary>
            <div class="sm:flex flex-row items-center transition-all">
                <FormField bind:value={event.latitude} extendClass="mr-4" label="Latitude" min="0" name="latitude"
                           type="number"/>
                <FormField bind:value={event.longitude} label="Longitude" min="0" name="longitude" type="number"/>
            </div>
        </details>
        <details class="my-4 transition-all">
            <summary class="font-semibold text-gray-600 text-sm transition-all">Recurrence</summary>
            <label class="font-semibold text-gray-600 text-sm" for="recurrenceDisabled">Has recurrence</label>
            <input bind:checked={hasRecurrenceChecked} class="input-field" id="recurrenceDisabled" name="allDay"
                   type="checkbox"/>
            <label class="font-semibold text-gray-600 text-sm" for="frequency">Frequency*</label>
            <select disabled={!hasRecurrenceChecked} bind:value={rec.freq} id="frequency">
                <option value="DAILY">Daily</option>
                <option value="WEEKLY">Weekly</option>
                <option value="MONTHLY">Monthly</option>
                <option value="YEARLY">Yearly</option>
            </select>
            <FormField disabled={!hasRecurrenceChecked} bind:value={rec.byMonth}
                       label="Months (from 1 to 12, comma separated)" name="bymonth"
                       type="text"/>
            <FormField disabled={!hasRecurrenceChecked} bind:value={rec.byYearDay}
                       label="Year days (from -366 to 366 except 0, comma separated)" name="byyearday"
                       type="text" extendClass="{rec.freq === 'YEARLY' ? '' : 'hidden'}"/>
            <FormField disabled={!hasRecurrenceChecked} bind:value={rec.byWeekNo}
                       label="Week numbers (from -53 to 53 except 0, comma separated)" name="byweekno"
                       type="text" extendClass="{rec.freq === 'YEARLY' ? '' : 'hidden'}"/>
            <FormField disabled={!hasRecurrenceChecked} bind:value={rec.byDay}
                       label="{rec.freq === 'YEARLY' || rec.freq === 'MONTHLY' ? 'Week days (week number from -53 to 53 except 0, followed by the day\'s first two letters all of which comma separated; e.g. +2MO,-51TH)' : 'Week days (e.g. MO or TH,WE,SA)'}"
                       name="byday"
                       type="text"/>
            <FormField disabled={!hasRecurrenceChecked} bind:value={rec.byMonthDay}
                       label="Month days" name="bymonthday" extendClass="{rec.freq === 'WEEKLY' ? 'hidden' : ''}"
                       type="text"/>
            <FormField disabled={!hasRecurrenceChecked} bind:value={rec.bySetPos}
                       label="Select specific occurrences (from -366 to 366 except 0, comma separated)" name="bysetpos"
                       type="text"/>
        </details>
        <Button extendClass="bg-pink-500 hover:bg-pink-600 mr-1" on:click={close}>Cancel</Button>
    </Form>
</div>

<style>
    details[open] summary ~ * {
        animation: sweep .5s ease-in-out;
    }

    @keyframes sweep {
        0% {
            opacity: 0;
            margin-left: -10px
        }
        100% {
            opacity: 1;
            margin-left: 0
        }
    }
</style>
