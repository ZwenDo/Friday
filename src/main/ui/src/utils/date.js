export function jsDateToFormDate(date) {
    return date.getFullYear() +
        "-" +
        insertPadding(date.getMonth() + 1) +
        "-" +
        insertPadding(date.getDate()) +
        "T" +
        insertPadding(date.getHours()) +
        ":" +
        insertPadding(date.getMinutes());
}

export function formDateToICalDate(date) {
    if (!date || date === "") return "";
    return date.replace(/[:-]/g, "") + "00Z";
}

export function dateToYearDay(it) {
    const date = new Date(it);
    return (Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()) - Date.UTC(date.getFullYear(), 0, 0)) / 24 / 60 / 60 / 1000;
}

function insertPadding(value) {
    return value < 10 ? "0" + value : "" + value;
}
