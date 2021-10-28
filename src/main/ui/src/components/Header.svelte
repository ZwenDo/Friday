<script>
    import Navbar from "./Navbar.svelte";
    import NavItem from "./NavItem.svelte";
</script>

<header class="w-full flex-initial flex justify-between">
    <h1 class="
        flex-initial
        font-cursive text-white text-7xl
        inline m-5 p-0
        transform hover:scale-110
        transition duration-200 ease-in-out"
    >
        <a href="/">Friday</a>
    </h1>
    <Navbar>
        <NavItem link="/overview" name="Overview"/>
        <NavItem link="/today" name="Today"/>
        <NavItem link="/month" name="This month"/>
    </Navbar>
</header>
