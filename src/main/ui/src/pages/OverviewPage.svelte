<script>
    import {getContext} from "svelte";
    import {logoutUser} from "../stores/login_store";
    import {COOKIE_USER_NAME, getCookie} from "../utils/cookies";
    import Button from "../components/Button.svelte";
    import EventForm from "../subparts/EventForm.svelte";
    import Heading from "../components/Heading.svelte";
    import Section from "../components/Section.svelte";
    import {push} from "svelte-spa-router";

    const {open} = getContext('simple-modal');

    function showEventForm() {
        open(EventForm, {}, {
            styleWindow: {
                width: '90vw',
                height: '90vh',
            },
            styleContent: {
                width: '100%',
                height: '100%',
            }
        });
    }

    function logout() {
        logoutUser();
        push("/login")
    }
</script>

<div class="p-4">
    <div class="sm:flex justify-between">
        <div>
            <Heading>Hello, {getCookie(COOKIE_USER_NAME)}!</Heading>
        </div>
        <div>
            <Button
                on:click={showEventForm}
            >
                Create Event
            </Button>
            <Button
                extendClass="bg-pink-500 hover:bg-pink-700 ml-4"
                on:click={logout}
            >
                Logout
            </Button>
        </div>
    </div>
    <Section title="Next">
        Lorem ipsum dolor sit amet, consectetur adipisicing elit. At consectetur consequatur consequuntur, error fuga
        labore maxime nobis numquam, quasi quis quo veniam voluptatibus. Excepturi, pariatur tempore? Architecto
        blanditiis consequatur deserunt distinctio earum error et illo, iste iusto libero modi nesciunt non officia,
        quaerat quam quis rerum sequi similique tenetur vel. Aut dolor dolores esse et fugiat, in laborum nesciunt nihil
        quis voluptatum. Aliquam dolorum facere incidunt odio quae? Aspernatur consequatur consequuntur dolor dolore
        dolores esse excepturi laborum libero minus necessitatibus, neque provident quaerat quasi quibusdam quidem
        sequi, tenetur ullam, veniam voluptas voluptates. A beatae iusto minima omnis quia tempora ut!
    </Section>
    <Section title="Today">
        Lorem ipsum dolor sit amet, consectetur adipisicing elit. At consectetur consequatur consequuntur, error fuga
        labore maxime nobis numquam, quasi quis quo veniam voluptatibus. Excepturi, pariatur tempore? Architecto
        blanditiis consequatur deserunt distinctio earum error et illo, iste iusto libero modi nesciunt non officia,
        quaerat quam quis rerum sequi similique tenetur vel. Aut dolor dolores esse et fugiat, in laborum nesciunt nihil
        quis voluptatum. Aliquam dolorum facere incidunt odio quae? Aspernatur consequatur consequuntur dolor dolore
        dolores esse excepturi laborum libero minus necessitatibus, neque provident quaerat quasi quibusdam quidem
        sequi, tenetur ullam, veniam voluptas voluptates. A beatae iusto minima omnis quia tempora ut!
    </Section>
    <Section title="This month">
        Lorem ipsum dolor sit amet, consectetur adipisicing elit. At consectetur consequatur consequuntur, error fuga
        labore maxime nobis numquam, quasi quis quo veniam voluptatibus. Excepturi, pariatur tempore? Architecto
        blanditiis consequatur deserunt distinctio earum error et illo, iste iusto libero modi nesciunt non officia,
        quaerat quam quis rerum sequi similique tenetur vel. Aut dolor dolores esse et fugiat, in laborum nesciunt nihil
        quis voluptatum. Aliquam dolorum facere incidunt odio quae? Aspernatur consequatur consequuntur dolor dolore
        dolores esse excepturi laborum libero minus necessitatibus, neque provident quaerat quasi quibusdam quidem
        sequi, tenetur ullam, veniam voluptas voluptates. A beatae iusto minima omnis quia tempora ut!
    </Section>
</div>
