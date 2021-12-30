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


function insertPadding(value) {
    return value < 10 ? "0" + value : "" + value;
}
