<div class="background-body"><div class="background-inner"></div></div>

<style>
    .background-body {
        background-color: #f0f0f0;
        width: 100vw;
        height: 100vh;
        position: absolute;
        top: 0;
        left: 0;
        overflow: hidden;
        z-index: -2;
    }

    .background-inner {
        background: linear-gradient(90deg, #5a1e8f, #7c14d2, #f369ad);
        background-size: 300% 300%;
        clip-path: polygon(0% 0%,100% 0%,100% 13%,0% 49%);
        animation: gradient 10s alternate infinite;
        height: 100%;
        z-index: -1;
    }

    @keyframes gradient {
        0% {
            background-position: 0;
        }
        100% {
            background-position: 100%;
        }
    }
</style>
