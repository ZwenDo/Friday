import tippy, {roundArrow} from 'tippy.js';

export function tooltip(node, params = {
    theme: "friday",
    arrow: roundArrow,
    duration: 100,
    interactive: true,
    animation: "friday",
}) {
    if (!node.title) {
        return {
            update: () => {
            },
            destroy: () => {
            },
        };
    }
    const content = node.title;
    node.title = "";
    const tip = tippy(node, {content, ...params});

    return {
        update: newParams => tip.setProps({content, ...newParams}),
        destroy: () => tip.destroy(),
    };
}
