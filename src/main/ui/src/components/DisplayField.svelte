<script>
    import {tooltip} from "../utils/tooltip";

    export let label = null;
    export let value = "";
    export let hint = null;
    export let extendClass = "";
    export let fieldClass = "";
</script>

<div class="mt-9 mb-4 relative {extendClass}">
    <span
        class="
            transition-all
            absolute left-2 -top-6
            font-semibold text-gray-600 text-sm"
    >
        {label}
    </span>
    <div
        class="input-field break-words {fieldClass}"
        title="{hint}" use:tooltip
    >
        {value}
    </div>
</div>
