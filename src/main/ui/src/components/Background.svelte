<div class="background-body"><div class="background-inner"></div></div>

<style>
    .background-body {
        background-color: rgb(240, 240, 240, 1);
        width: 100vw;
        height: 100vh;
        position: absolute;
        overflow: hidden;
        z-index: -2;
        top: 0;
        left: 0;
    }

    .background-inner {
        transform: rotate(-12deg) scale(1.5) translateY(-45%);
        background: linear-gradient(90deg, #5a1e8f, #6411a9, #f369ad);
        background-size: 300% 300%;
        animation: gradient 11s alternate infinite;
        position: absolute;
        top: 0;
        left: 0;
        height: 60%;
        width: 100%;
        z-index: -1;
    }

    @keyframes gradient {
        0% {
            background-position: 0%;
        }
        100% {
            background-position: 100%;
        }
    }
</style>
