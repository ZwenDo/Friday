module.exports = {
    mode: 'jit',
    purge: ['./src/**/*.{js,svelte}', './public/index.html'],
    darkMode: false, // or 'media' or 'class'
    theme: {
        fontFamily: {
            cursive: ['Playball', 'cursive'],
            serif: ['Raleway', 'serif']
        },
        extend: {
            width: {
                fit: "fit-content"
            },
            backgroundSize: {
                '300': '200%'
            },
            zIndex: {
                '-20': '-20',
                '-10': '-10'
            },
            animation: {
                'alternate': 'fader 10s alternate infinite'
            },
            keyframes: {
                fader: {
                    '0%': {'background-position': '0%'},
                    '100%': {'background-position': '100%'}
                }
            },
            spacing: {
                '100': '33rem'
            },
            boxShadow: {
                inner: 'inset 0 2px 4px 0 rgba(0, 0, 0, 0.5)'
            }
        },
    },
    variants: {
        extend: {},
    },
    plugins: [],
};
