import {sendHTTPRequest} from "../utils/http_requests";
import {COOKIE_USER_ID, COOKIE_USER_TOKEN, getCookie} from "../utils/cookies";

const api = "/api/event/";

export function createEvent(
    title,
    description,
    place,
    startDate,
    endDate,
    latitude,
    longitude,
    recurRuleParts,
    onSuccess,
    onFail
) {
    const userId = getCookie(COOKIE_USER_ID);
    const userToken = getCookie(COOKIE_USER_TOKEN);
    sendHTTPRequest(
        api,
        "POST",
        {
            userId,
            userToken,
            title,
            description,
            place,
            startDate,
            recurRuleParts,
            latitude,
            longitude,
            endDate
        },
        201,
        onSuccess,
        onFail
    );
}

export function allByUser(onSuccess) {
    const userId = getCookie(COOKIE_USER_ID);
    const token = getCookie(COOKIE_USER_TOKEN);
    sendHTTPRequest(
        api + "allbyuser",
        "POST",
        {
            userId,
            token
        },
        200,
        res => {
            res.json().then(data => onSuccess(data));
        },
        () => {
        },
    );
}
