<script>
    import Router from "svelte-spa-router";
    import {routes} from "../utils/router";
</script>

<main
    class="
    flex-auto flex flex-col
    bg-white
    z-0 px-5
    m-auto w-11/12
    rounded-lg shadow-2xl
    overflow-y-auto"
>
    <Router {routes}/>
</main>
