<script>
    import {getContext, onMount} from "svelte";
    import {formDateToICalDate, jsDateToFormDate} from "../utils/date";
    import {createEvent, updateEvent} from "../stores/event_store";
    import Button from "../components/Button.svelte";
    import Checkbox from "../components/Checkbox.svelte";
    import Details from "../components/Details.svelte";
    import Form from "../components/Form.svelte";
    import FormField from "../components/FormField.svelte";

    const {close} = getContext('simple-modal');

    export let calendarRefs;
    export let eventToEdit = undefined;

    const converter = {
        "FREQ": "freq",
        "UNTIL": "until"
    }

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

    let rec = {
        freq: "NONE"
    };

    function submit() {
        if (rec.freq !== "NONE") {
            if (!rec["until"]) delete rec["until"];
            event.rrule = Object.keys(rec).map(k => `${k.toUpperCase()}=${rec[k]}`).join(";");
        }
        event.end = event.allDay ? null : event.end;
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

    let untilForm;
    $: rec.until = formDateToICalDate(untilForm);

    function parseRrule(rrule) {
        if (!rrule) {
            rec.freq = "NONE";
            return;
        }
        const rules = rrule.split(";");
        rules.forEach(r => {
            const pair = r.split("=");
            const key = converter[pair[0]];
            if (!key) return;
            rec[key] = pair[1];
        });
        if (!rec["until"]) rec["until"] = null;
    }

    onMount(() => {
        if (eventToEdit) {
            event = {...eventToEdit};
            parseRrule(eventToEdit.rrule);
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
            <div class="grid gap-x-4 grid-cols-1 sm:grid-cols-2 mt-2">
                <div class="mt-8 mb-4 relative">
                    <label class="absolute left-2 -top-6 font-semibold text-gray-600 text-sm"
                           for="frequency">Frequency*</label>
                    <select bind:value={rec.freq} class="mt-1 input-field" id="frequency">
                        <option value="NONE">None</option>
                        <option value="DAILY">Daily</option>
                        <option value="WEEKLY">Weekly</option>
                        <option value="MONTHLY">Monthly</option>
                        <option value="YEARLY">Yearly</option>
                    </select>
                </div>
                <FormField  bind:value={untilForm} disabled={rec.freq === "NONE"} fieldClass="full-field"
                            label="Until" name="until" type="datetime-local"/>
            </div>
        </Details>
        <Button extendClass="bg-pink-500 hover:bg-pink-600 mr-1" on:click={close}>Cancel</Button>
    </Form>
</div>
