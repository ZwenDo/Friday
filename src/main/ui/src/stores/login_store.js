import {
    COOKIE_USER_ID,
    COOKIE_USER_NAME,
    COOKIE_USER_TOKEN,
    deleteCookies,
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
            deleteCookies();
        }
    );
}

export function logoutUser() {
    return logout();
}

export function logoutAll() {
    return logout(true);
}

export function isLoginValid(onFail) {
    const userId = getCookie(COOKIE_USER_ID);
    const token = getCookie(COOKIE_USER_TOKEN);
    sendHTTPRequest(
        api + "check",
        "POST",
        {
            userId,
            token
        },
        202,
        () => {},
        onFail
    );
}
