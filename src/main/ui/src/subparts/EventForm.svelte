<script>
    import Form from "../components/Form.svelte";
    import FormField from "../components/FormField.svelte";
    import {getContext} from "svelte";
    import Button from "../components/Button.svelte";
    import {createEvent} from "../stores/event_store";

    const {close} = getContext('simple-modal');

    export let calendarRef;

    let title;
    let description = null;
    let place = null;
    let startDate;
    let endDate;
    let latitude = 50;
    let longitude = 27.3;
    let recurRuleParts = null;

    function createNewEvent() {
        createEvent(
            title,
            description,
            place,
            startDate,
            endDate,
            latitude,
            longitude,
            recurRuleParts,
            _ => {
                calendarRef.getAPI().refetchEvents();
            },
            _ => { }
        );
        close();
    }
</script>

<div class="w-full h-full flex justify-center items-center">
    <Form on:submit={createNewEvent} submitText="Create" title="Create an event">
        <div class="sm:flex">
            <div class="sm:pr-4">
                <FormField bind:value={title} label="Title" name="title" required="true" type="text"/>
                <FormField bind:value={description} label="Description" name="description" type="textarea 18 5"/>
            </div>
            <div>
                <FormField bind:value={place} label="Place" name="place" type="text"/>
                <FormField bind:value={startDate} label="Start Date" name="startDate" required="true" type="datetime-local"/>
                <FormField bind:value={endDate} label="End Date" name="endDate" required="true" type="datetime-local"/>
            </div>
        </div>
        <Button extendClass="bg-pink-500 hover:bg-pink-600 mr-1" on:click={close}>Cancel</Button>
    </Form>
</div>
