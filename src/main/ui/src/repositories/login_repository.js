import {deleteCookie, getCookie, setCookie} from "../utils/cookies";
import {booleanHTTPRequest} from "../utils/http_requests";

const api = "/api/auth/";

export function loginUser(username, password) {
    return booleanHTTPRequest(
        api + "login",
        "POST",
        {
            username,
            password
        },
        201,
        response => {
            setCookie("friday-userId", response['userId']);
            setCookie("friday-userToken", response['token']);
        }
    );
}

function logout(all = false) {
    return booleanHTTPRequest(
        api + "logout" + (all ? "/all" : ""),
        "POST",
        {
            userId: getCookie("friday-userId"),
            token: getCookie("friday-userToken")
        },
        202,
        _ => {
            deleteCookie("friday-userId");
            deleteCookie("friday-userToken");
        }
    );
}

export function logoutUser() {
    return logout();
}

export function logoutAll() {
    return logout(true);
}