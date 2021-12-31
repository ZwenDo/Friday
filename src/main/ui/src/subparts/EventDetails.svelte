<script>
    import {getContext} from "svelte";
    import Button from "../components/Button.svelte";
    import DisplayField from "../components/DisplayField.svelte";
    import Heading from "../components/Heading.svelte";
    import Details from "../components/Details.svelte";
    import EventForm from "./EventForm.svelte";
    import {deleteEvent} from "../stores/event_store";

    const {open, close} = getContext('simple-modal');

    export let calendarRefs = [];
    export let event;

    const start = new Date(event.start);
    const actualStart = event.allDay
        ? start.toLocaleDateString()
        : `${start.toLocaleDateString()} ${start.toLocaleTimeString()}`

    const end = new Date(event.end);
    const actualEnd = `${end.toLocaleDateString()} ${end.toLocaleTimeString()}`;

    const userOrigin = {
        latitude: null,
        longitude: null
    };
    if (event.place || (event.latitude && event.longitude)) {
        navigator.geolocation.getCurrentPosition(position => {
            userOrigin.latitude = position.coords.latitude;
            userOrigin.longitude = position.coords.longitude;
        });
    }

    let location = null;
    if (event.latitude && event.longitude) {
        location = `${event.latitude},${event.longitude}`;
    } else if (event.place) {
        location = event.place;
    }

    let mapurl = `https://www.google.com/maps/embed/v1/directions?mode=transit&origin=${userOrigin.latitude},${userOrigin.longitude}&destination=${location}&key=${process.env.GMAP_KEY}`;

    function gotoEdit() {
        close();
        open(EventForm, {calendarRefs, eventToEdit: event}, {
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
    }

    function gotoDelete() {
        deleteEvent(event.id, _ => {
            calendarRefs.forEach(c => c.getAPI().refetchEvents());
            close();
        })
    }
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
    <Details extendClass="mt-4" name="Map">
        {#if location && userOrigin.latitude && userOrigin.longitude}
            <iframe title="Event map"
                    class="w-full" height="500"
                    style="border:0" frameborder="0"
                    src={mapurl} allowfullscreen></iframe>
        {:else}
            <p class="m-7 text-lg font-thin">
                Cannot get directions :
                {#if !location} no location specified for this event{:else} cannot get your location{/if}
            </p>
        {/if}
    </Details>
    <div class="w-full h-full flex justify-start items-center mt-4">
        <Button extendClass="bg-pink-500 hover:bg-pink-600 mr-2" on:click={close}>Close</Button>
        <Button extendClass="bg-purple-500 hover:bg-purple-600 mr-2" on:click={gotoEdit}>Edit</Button>
        <Button extendClass="bg-red-600 hover:bg-red-700 mr-2" on:click={gotoDelete}>Delete</Button>
    </div>
</div>
