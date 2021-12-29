<script>
    import Form from "../components/Form.svelte";
    import FormField from "../components/FormField.svelte";
    import {getContext} from "svelte";
    import Button from "../components/Button.svelte";
    import {createEvent} from "../stores/event_store";

    const {close} = getContext('simple-modal');

    export let calendarRef;

    let event = {
        title: undefined,
        description: null,
        place: null,
        startDate: undefined,
        endDate: undefined,
        allDay: false,
        latitude: null,
        longitude: null,
        recurRuleParts: null
    };

    let endDisabled;
    $: endDisabled = event.allDay;

    function createNewEvent() {
        createEvent(...event, _ => calendarRef.getAPI().refetchEvents(), _ => {
        });
        close();
    }
</script>

<div class="w-full h-full flex justify-center items-center">
    <Form on:submit={createNewEvent} submitText="Create" title="Create an event">
        <div class="sm:flex">
            <div class="sm:pr-4">
                <FormField bind:value={event.title} label="Title" name="title" required="true" type="text"/>
                <FormField bind:value={event.description} label="Description" name="description" type="textarea 18 5"/>
            </div>
            <div>
                <FormField bind:value={event.place} label="Place" name="place" type="text"/>
                <FormField bind:value={event.startDate} label="Start Date" name="startDate" required="true"
                           type="datetime-local"/>
                <FormField bind:disabled="{endDisabled}" bind:value={event.endDate} label="End Date" name="endDate"
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
        <Button extendClass="bg-pink-500 hover:bg-pink-600 mr-1" on:click={close}>Cancel</Button>
    </Form>
</div>
