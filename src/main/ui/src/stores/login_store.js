import {
    COOKIE_USER_ID,
    COOKIE_USER_NAME,
    COOKIE_USER_TOKEN,
    deleteCookie,
    getCookie,
    setCookie
} from "../utils/cookies";
import {sendHTTPRequest} from "../utils/http_requests";

const api = "/api/auth/";

export function loginUser(username, password, onFail, onSuccess) {
    sendHTTPRequest(
        api + "login",
        "POST",
        {
            username,
            password
        },
        201,
        response => response.json().then(data => {
            setCookie(COOKIE_USER_ID, data['userId']);
            setCookie(COOKIE_USER_TOKEN, data['token']);
            setCookie(COOKIE_USER_NAME, username);
            onSuccess(data);
        }),
        onFail
    );
}

function logout(all = false) {
    const userId = getCookie(COOKIE_USER_ID);
    const token = getCookie(COOKIE_USER_TOKEN);
    return sendHTTPRequest(
        api + "logout" + (all ? "/all" : ""),
        "POST",
        {
            userId,
            token
        },
        202,
        _ => {
            deleteCookie(COOKIE_USER_ID);
            deleteCookie(COOKIE_USER_TOKEN);
            deleteCookie(COOKIE_USER_NAME);
        }
    );
}

export function logoutUser() {
    return logout();
}

export function logoutAll() {
    return logout(true);
}
