<script>
    import Form from "../components/Form.svelte";
    import FormField from "../components/FormField.svelte";
    import {getContext} from "svelte";
    import Button from "../components/Button.svelte";

    const {close} = getContext('simple-modal');

    let title;
    let description;
    let place;
    let startDate;
    let duration;

    function createEvent() {
        // TODO send event to api
        close();
    }
</script>

<div class="w-full h-full flex justify-center items-center">
    <Form on:submit={createEvent} submitText="Create" title="Create an event">
        <div class="sm:flex">
            <div class="sm:pr-4">
                <FormField bind:value={title} label="Title" name="title" required="true" type="text"/>
                <FormField bind:value={description} label="Description" name="description" type="textarea 18 5"/>
            </div>
            <div>
                <FormField bind:value={place} label="Place" name="place" type="text"/>
                <FormField bind:value={startDate} label="Start Date" name="startDate" required="true" type="date"/>
                <FormField bind:value={duration} label="Duration" name="duration" required="true" type="number"/>
            </div>
        </div>
        <Button extendClass="bg-pink-500 hover:bg-pink-600 mr-1" on:click={close}>Cancel</Button>
    </Form>
</div>