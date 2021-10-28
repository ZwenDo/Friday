<script>
import NavItem from "./NavItem.svelte";

</script>

<nav class="
    flex-auto
    inline-block mr-8
    flex flex-row items-center justify-around"
>
    <NavItem link="/overview" name="Overview"/>
    <NavItem link="/today" name="Today"/>
    <NavItem link="/month" name="This month"/>
</nav>