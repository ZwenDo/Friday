<script>
    import {getContext} from "svelte";
    import Heading from "../components/Heading.svelte";
    import Button from "../components/Button.svelte";
    import FormField from "../components/FormField.svelte";
    import {importFromFile, importFromURL} from "../stores/event_store";
    import {importFromGoogleCalendar} from "../stores/googlecalendar_store";

    const {close} = getContext('simple-modal');

    export let calendarRefs;

    let url;
    let gmail;
    let data;
    $: console.log(data);
    let files;
    $: {
        if (files && files[0]) {
            const file = files[0];
            const reader = new FileReader();
            reader.onload = function (evt) {
                data = evt.target.result;
            }
            reader.readAsText(file);
        }
    }

    function sendFile() {
        importFromFile(data, refreshCalendars);
    }

    function sendURL() {
        importFromURL(url, refreshCalendars);
    }

    function sendGoogle() {
        importFromGoogleCalendar(gmail, refreshCalendars);
    }

    function refreshCalendars() {
        calendarRefs.forEach(c => c.getAPI().refetchEvents());
        close();
    }
</script>

<div class="
    main-container font-serif
    w-full h-full
    flex flex-auto flex-col
    transition-all"
>
    <Heading>Import Events</Heading>
    <hr class="mt-4"/>
    <div class="grid grid-cols-3 gap-x-4">
        <div class="mt-9 mb-4 relative col-span-2">
            <input bind:files class="input-field full-field" id="fileinput" type="file"/>
            <label
                class="
                transition-all
                absolute left-2 -top-6
                font-semibold text-gray-600 text-sm"
                for="fileinput"
            >
                From ics File
            </label>
        </div>
        <div class="flex justify-start items-center pt-5">
            <Button on:click={sendFile}>Import</Button>
        </div>

        <FormField bind:value={url} extendClass="col-span-2" fieldClass="full-field" hint="iCalendar feed"
                   label="From URL" name="url" type="text"/>
        <div class="flex justify-start items-center pt-5">
            <Button on:click={sendURL}>Import</Button>
        </div>

        <FormField bind:value={gmail} extendClass="col-span-2" fieldClass="full-field" hint="GMail address"
                   label="From Google Calendar" name="gmail" type="email"/>
        <div class="flex justify-start items-center pt-5">
            <Button on:click={sendGoogle}>Import</Button>
        </div>
    </div>

    <Button on:click={close}>Cancel</Button>
</div>
