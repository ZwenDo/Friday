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
        e => console.log("create event failed because of:\n" + e)
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
        res => res.json().then(data => onSuccess(data)),
        (e) => console.log("all by user failed because of:\n" + e)
    );
}

export function updateEvent(event, onSuccess) {
    event.userId = getCookie(COOKIE_USER_ID);
    event.userToken = getCookie(COOKIE_USER_TOKEN);
    sendHTTPRequest(
        api + "update/" + event.id,
        "PUT",
        event,
        200,
        onSuccess,
        e => console.log("update event failed because of:\n" + e)
    );
}

export function deleteEvent(id, onSuccess) {
    let body = {};
    body.userId = getCookie(COOKIE_USER_ID);
    body.token = getCookie(COOKIE_USER_TOKEN);
    sendHTTPRequest(
        api + "delete/" + id,
        "DELETE",
        body,
        204,
        onSuccess,
        e => console.log("delete event failed because of:\n" + e)
    );
}
