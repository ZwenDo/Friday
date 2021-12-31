import {formDateToICalDate} from "./date";

export const converter = {
    "FREQ": "freq",
    "BYYEAR": "byYear",
    "BYMONTH": "byMonth",
    "BYYEARDAY": "byYearDay",
    "BYWEEKNO": "byWeekNo",
    "BYDAY": "byDay",
    "BYMONTHDAY": "byMonthDay",
    "BYSETPOS": "bySetPos"
}

export function createRrule(rec) {
    let rrule = "FREQ=" + rec.freq;
    switch (rec.freq) {
        case "YEARLY":
            if (
                !checkWeekNo(rec) ||
                !checkYearDays(rec) ||
                !(!rec.byWeekNo && checkDaysWithWeekNo(rec) || checkDays(rec)) ||
                !checkMonthDays(rec) ||
                !checkMonths(rec)
            ) {
                return null;
            } else {
                rrule += addIfNotEmpty(rec.byWeekNo, ";BYWEEKNO=") +
                    addIfNotEmpty(rec.byYearDay, ";BYYEARDAY=") +
                    addIfNotEmpty(rec.byMonthDay, ";BYMONTHDAY=") +
                    addIfNotEmpty(rec.byMonth, ";BYMONTH=") +
                    addIfNotEmpty(rec.byDay, ";BYDAY=");
            }
            break;
        case "MONTHLY":
            if (
                !checkDaysWithWeekNo(rec) ||
                !checkMonthDays(rec) ||
                !checkMonths(rec)
            ) {
                return null;
            } else {
                rrule += addIfNotEmpty(rec.byMonthDay, ";BYMONTHDAY=") +
                    addIfNotEmpty(rec.byMonth, ";BYMONTH=") +
                    addIfNotEmpty(rec.byDay, ";BYDAY=");
            }
            break;
        case "DAILY":
            if (
                !checkDays(rec) ||
                !checkMonthDays(rec) ||
                !checkMonths(rec)
            ) {
                return null;
            } else {
                rrule += addIfNotEmpty(rec.byMonthDay, ";BYMONTHDAY=") +
                    addIfNotEmpty(rec.byMonth, ";BYMONTH=") +
                    addIfNotEmpty(rec.byDay, ";BYDAY=");
            }
            break;
        case "WEEKLY":
            if (
                !checkDays(rec) ||
                !checkMonths(rec)
            ) {
                return null;
            } else {
                rrule += addIfNotEmpty(rec.byMonth, ";BYMONTH=") +
                    addIfNotEmpty(rec.byDay, ";BYDAY=");
            }
            break;
    }
    if (!checkSetPos(rec)) return null;
    return rrule + addIfNotEmpty(rec.bySetPos, ";BYSETPOS=") + addIfNotEmpty(formDateToICalDate(rec.until), ";UNTIL=");
}

function addIfNotEmpty(str, prefix) {
    return str.length > 0 ? prefix + str : "";
}

function checkDaysWithWeekNo(rec) {
    if (rec.byDay.length === 0) return true;
    if (rec.byDay.split(",").some(e => !/[+-]?([1-4]?[0-9]|5[0-3])?(MO|TU|WE|TH|FR|SA|SU)/.exec(e))) {
        alert("Invalid days");
        return false;
    }
    return true;
}

function checkDays(rec) {
    if (rec.byDay.length === 0) return true;
    const days = rec.byDay.split(",");
    if (days.some(d => d !== "MO" && d !== "TU" && d !== "WE" && d !== "TH" && d !== "FR" && d !== "SA" && d !== "SU")) {
        alert("Invalid days");
        return false;

    }
    return true;
}

function checkMonths(rec) {
    if (rec.byMonth.length === 0) return true;

    const months = rec.byMonth.split(",");
    if (months.some(m => !Number.isFinite(+m) || +m < 1 || +m > 12)) {
        alert("Invalid months rule.");
        return false;
    }
    return true;
}

function checkWeekNo(rec) {
    if (rec.byWeekNo.length === 0) return true;

    const weeksNo = rec.byWeekNo.split(",");
    if (weeksNo.some(w => !Number.isFinite(+w) || +w === 0 || +w < -53 || +w > 53)) {
        alert("Invalid week numbers rule.");
        return false;
    }
    return true;
}

function checkYearDaysRange(string, message) {
    if (string.length === 0) return true;

    const array = string.split(",");
    if (array.some(d => !Number.isFinite(+d) || +d === 0 || +d > 366 || +d < -366)) {
        alert(message);
        return false;
    }
    return true;
}

function checkYearDays(rec) {
    return checkYearDaysRange(rec.byYearDay, "Invalid year days");
}

function checkSetPos(rec) {
    return checkYearDaysRange(rec.bySetPos, "Invalid occurrence numbers");
}

function checkMonthDays(rec) {
    if (rec.byMonthDay.length === 0) return true;

    const monDays = rec.byMonthDay.split(",");
    if (monDays.some(d => !Number.isFinite(+d) || +d < 1 || +d > 31)) {
        alert("Invalid month days");
        return false
    }
    return true;
}
