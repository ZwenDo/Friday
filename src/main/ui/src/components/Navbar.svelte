<script>
    import NavItem from './NavItem.svelte';
    import { onMount } from 'svelte';

    export let menuChoices;

    let menu = {};

    function resetMenu() {
        Object.keys(menu).forEach((choice, _) => (menu[choice] = false));
    }

    function chooseMenu(which) {
        resetMenu();
        menu[which] = true;
    }

    function choiceToLink(choice) {
        return choice.toLowerCase().replace(/\s/g, '');
    }

    onMount(() => {
        for (const it of menuChoices) {
            menu[it] = false;
        }
        menu[menuChoices[0]] = true;
    });
</script>

<nav
    class="
    right-20 relative top-10 w-100
    flex items-center justify-around"
>
    {#each Object.keys(menu) as choice}
        <NavItem
            on:click="{() => chooseMenu(choice)}"
            selected="{menu[choice]}"
            link="{choiceToLink(choice)}"
            name="{choice}"
        />
    {/each}
</nav>
