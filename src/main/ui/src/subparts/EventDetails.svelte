<script>
    import {getContext} from "svelte";
    import Button from "../components/Button.svelte";

    const {close} = getContext('simple-modal');

    export let calendarRefs = null;
    export let event;

    /* TODO layout
        TITLE, DESC, PLACE, START, END, GEO BUTTON
    */
</script>

<div class="w-full h-full flex justify-center items-center">
    <Button extendClass="bg-pink-500 hover:bg-pink-600 mr-1" on:click={close}>Delete</Button>
    <Button extendClass="bg-pink-500 hover:bg-pink-600 mr-1" on:click={close}>Edit</Button>
</div>
