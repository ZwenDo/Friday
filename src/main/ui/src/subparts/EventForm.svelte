<script>
    import {getContext, onMount} from "svelte";
    import {converter, createRrule} from "../utils/rrule";
    import {jsDateToFormDate} from "../utils/date";
    import {createEvent, updateEvent} from "../stores/event_store";
    import Button from "../components/Button.svelte";
    import Checkbox from "../components/Checkbox.svelte";
    import Details from "../components/Details.svelte";
    import Form from "../components/Form.svelte";
    import FormField from "../components/FormField.svelte";

    const {close} = getContext('simple-modal');

    export let calendarRefs;
    export let eventToEdit = undefined;


    let event = {
        title: null,
        description: null,
        place: null,
        start: jsDateToFormDate(new Date()),
        end: jsDateToFormDate(new Date()),
        allDay: false,
        latitude: null,
        longitude: null,
        rrule: null,
    };

    let hasRecurrenceChecked = false;

    let rec = {
        freq: "WEEKLY",
        byMonth: "",
        byYearDay: "",
        byWeekNo: "",
        byDay: "",
        byMonthDay: "",
        bySetPos: "",
        until: null,
    };

    function submit() {
        if (hasRecurrenceChecked) {
            event.rrule = createRrule(rec);
            if (event.rrule == null) return; // stop creation
        }
        event.end = event.allDay ? null : event.end;
        event.allDay = null;
        if (!eventToEdit) {
            createEvent(event, refreshCalendars);
        } else {
            updateEvent(event, refreshCalendars);
        }
    }

    function refreshCalendars() {
        calendarRefs.forEach(c => c.getAPI().refetchEvents());
        close();
    }

    function parseRrule(rrule) {
        if (!rrule) return;
        const rules = rrule.split(";");
        rules.forEach(r => {
            const pair = r.split("=");
            const key = converter[pair[0]];
            if (!key) return;
            rec[key] = pair[1];
        });
    }

    onMount(() => {
        if (eventToEdit) {
            event = {...eventToEdit};
            parseRrule(eventToEdit.rrule);
            hasRecurrenceChecked = event.rrule !== undefined;
        }
    });
</script>

<div>
    <Form on:submit={submit} submitText="{eventToEdit ? 'Update' : 'Create'}"
          title="{eventToEdit ? 'Edit' : 'Create'} an event">
        <div class="sm:flex">
            <div class="sm:pr-4">
                <FormField bind:value={event.title} fieldClass="full-field" label="Title*"
                           name="title" required="true" type="text"/>
                <FormField bind:value={event.description} fieldClass="full-field" label="Description"
                           name="description" type="textarea 19 6"/>
            </div>
            <div>
                <FormField bind:value={event.place} fieldClass="full-field" label="Place"
                           name="place" type="text"/>
                <FormField bind:value={event.start} fieldClass="full-field" label="Start Date*"
                           name="startDate" required="true" type="datetime-local"/>
                <FormField bind:value={event.end} disabled="{event.allDay}" fieldClass="full-field" label="End Date*"
                           name="endDate" required="{!event.allDay}" type="datetime-local"/>
                <Checkbox bind:checked={event.allDay} label="All Day" name="allDay"/>
            </div>
        </div>
        <Details extendClass="my-4" name="Localisation">
            <div class="grid gap-x-4 grid-cols-1 sm:grid-cols-2">
                <FormField bind:value={event.latitude} fieldClass="full-field" label="Latitude" min="0"
                           name="latitude" type="number"/>
                <FormField bind:value={event.longitude} fieldClass="full-field" label="Longitude" min="0"
                           name="longitude" type="number"/>
            </div>
        </Details>
        <Details extendClass="my-4" name="Recurrence">
            <Checkbox bind:checked={hasRecurrenceChecked} label="Has recurrence" name="hasRecurrence"/>
            <div class="grid gap-x-4 grid-cols-1 sm:grid-cols-2 mt-2">
                <div class="mt-8 mb-4 relative {!hasRecurrenceChecked ? 'opacity-25' : ''}">
                    <label class="absolute left-2 -top-6 font-semibold text-gray-600 text-sm"
                           for="frequency">Frequency*</label>
                    <select bind:value={rec.freq} class="mt-1 input-field" disabled={!hasRecurrenceChecked}
                            id="frequency">
                        <option value="DAILY">Daily</option>
                        <option value="WEEKLY">Weekly</option>
                        <option value="MONTHLY">Monthly</option>
                        <option value="YEARLY">Yearly</option>
                    </select>
                </div>
                <FormField bind:value={rec.byMonth} disabled={!hasRecurrenceChecked} fieldClass="full-field"
                           hint="From 1 to 12, comma separated"
                           label="Months" name="bymonth" type="text"/>
                <FormField bind:value={rec.byYearDay} disabled={!hasRecurrenceChecked}
                           extendClass="{rec.freq === 'YEARLY' ? '' : 'hidden'}"
                           fieldClass="full-field"
                           hint="From -366 to 366 except 0, comma separated"
                           label="Year days" name="byyearday" type="text"/>
                <FormField bind:value={rec.byWeekNo} disabled={!hasRecurrenceChecked}
                           extendClass="{rec.freq === 'YEARLY' ? '' : 'hidden'}"
                           fieldClass="full-field"
                           hint="From -53 to 53 except 0, comma separated"
                           label="Week numbers" name="byweekno" type="text"/>
                {#if rec.freq === 'YEARLY' || rec.freq === 'MONTHLY'}
                    <FormField fieldClass="full-field" disabled={!hasRecurrenceChecked} bind:value={rec.byDay}
                               hint="Week number from -53 to 53 except 0, followed by the day's first two letters all of which comma separated; e.g. +2MO,-51TH"
                               label="Week days" name="byday" type="text"/>
                {:else}
                    <FormField fieldClass="full-field" disabled={!hasRecurrenceChecked} bind:value={rec.byDay}
                               hint="MO or TH,WE,SA"
                               label="Week days" name="byday" type="text"/>
                {/if}
                <FormField bind:value={rec.byMonthDay} disabled={!hasRecurrenceChecked}
                           extendClass="{rec.freq === 'WEEKLY' ? 'hidden' : ''}"
                           fieldClass="full-field"
                           hint="From -31 to 31 except 0, comma separated" label="Month days"
                           name="bymonthday" type="text"/>
                <FormField bind:value={rec.bySetPos} disabled={!hasRecurrenceChecked} fieldClass="full-field"
                           hint="From -366 to 366 except 0, comma separated ; to select specific occurrences of the actual rule"
                           label="Specific occurrences" name="bysetpos" type="text"/>
                <FormField  bind:value={rec.until} disabled={!hasRecurrenceChecked} fieldClass="full-field"
                            label="Until" name="until" type="datetime-local"/>
            </div>
        </Details>
        <Button extendClass="bg-pink-500 hover:bg-pink-600 mr-1" on:click={close}>Cancel</Button>
    </Form>
</div>
