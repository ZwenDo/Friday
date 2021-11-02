module.exports = {
    mode: 'jit',
    purge: ['./src/**/*.{js,svelte}', './public/index.html'],
    darkMode: false, // or 'media' or 'class'
    theme: {
        fontFamily: {
            cursive: ['Great Vibes', 'cursive'],
            serif: ['Raleway', 'serif']
        },
        extend: {
            backgroundSize: {
                '300': '300%'
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
                    '0%': {'background-position': 0},
                    '100%': {'background-position': '100%'}
                }
            },
            spacing: {
                '100': '33rem'
            }
        },
    },
    variants: {
        extend: {},
    },
    plugins: [],
};
