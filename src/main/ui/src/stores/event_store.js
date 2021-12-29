import {sendHTTPRequest} from "../utils/http_requests";
import {COOKIE_USER_ID, COOKIE_USER_TOKEN, getCookie} from "../utils/cookies";

const api = "/api/event/";

export function createEvent(
    event,
    onSuccess,
) {
    event.userId = getCookie(COOKIE_USER_ID);
    event.userToken = getCookie(COOKIE_USER_TOKEN);
    sendHTTPRequest(
        api,
        "POST",
        event,
        201,
        onSuccess,
        () => {}
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
