import {loginUser} from "./login_repository";
import {booleanHTTPRequest} from "../utils/http_requests";
import {COOKIE_USER_ID, getCookie} from "../utils/cookies";

const api = "/api/user/";

export function registerUser(username, password) {
    return booleanHTTPRequest(
        api + "save",
        "POST",
        {
            username,
            password
        },
        201,
        _ => loginUser(username, password)
    );
}

export function deleteUser(password) {
    return booleanHTTPRequest(
        api + "delete/" + getCookie(COOKIE_USER_ID),
        "DELETE",
        {
            password
        },
        204
    );
}

export function updateUserPassword(oldPassword, newPassword) {
    return booleanHTTPRequest(
        api + "update/" + getCookie(COOKIE_USER_ID),
        "PUT",
        {
            oldPassword,
            newPassword
        },
        204
    );
}