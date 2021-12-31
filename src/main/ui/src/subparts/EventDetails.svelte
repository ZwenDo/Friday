<script>
    import {getContext} from "svelte";
    import Button from "../components/Button.svelte";
    import DisplayField from "../components/DisplayField.svelte";
    import Heading from "../components/Heading.svelte";
    import Details from "../components/Details.svelte";

    const {close} = getContext('simple-modal');

    export let calendarRefs = null;
    export let event;

    const start = new Date(event.start);
    const actualStart = event.allDay
        ? start.toLocaleDateString()
        : `${start.toLocaleDateString()} ${start.toLocaleTimeString()}`

    const end = new Date(event.end);
    const actualEnd = `${end.toLocaleDateString()} ${end.toLocaleTimeString()}`;
</script>

<div class="w-full h-full flex flex-col">
    <Heading>Event</Heading>
    <div class="sm:flex mt-4">
        <div class="sm:pr-4 sm:flex-1">
            <DisplayField fieldClass="full-field" label="Title" value={event.title}/>
            {#if event.description}
                <DisplayField value={event.description} fieldClass="full-field overflow-hidden"
                              label="Description"/>
            {/if}
        </div>
        <div class="sm:flex-1">
            {#if event.place}
                <DisplayField value={event.place} fieldClass="full-field" label="Place"/>
            {/if}
            <DisplayField fieldClass="full-field" label="{event.allDay ? '' : 'Start '}Date" value={actualStart}/>
            {#if !event.allDay}
                <DisplayField value={actualEnd} fieldClass="full-field" label="End Date"/>
            {/if}
        </div>
    </div>
    {#if event.latitude && event.longitude}
        <Details extendClass="mt-4" name="Map">
            <iframe src="https://hin.slama.io"></iframe>
        </Details>
    {/if}
    <div class="w-full h-full flex justify-start items-center mt-4">
        <Button extendClass="bg-pink-500 hover:bg-pink-600 mr-2" on:click={close}>Close</Button>
        <Button extendClass="bg-purple-500 hover:bg-purple-600 mr-2" on:click={close}>Edit</Button>
        <Button extendClass="bg-red-600 hover:bg-red-700 mr-2" on:click={close}>Delete</Button>
    </div>
</div>
