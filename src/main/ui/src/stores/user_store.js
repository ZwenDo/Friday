import {loginUser} from "./login_store";
import {sendHTTPRequest} from "../utils/http_requests";
import {COOKIE_USER_ID, getCookie} from "../utils/cookies";

const api = "/api/user/";

export function registerUser(username, password, onFail, onSuccess) {
    return sendHTTPRequest(
        api + "save",
        "POST",
        {
            username,
            password
        },
        201,
        _ => loginUser(username, password, _ => { }, onSuccess),
        onFail
    );
}

export function deleteUser(password) {
    return sendHTTPRequest(
        api + "delete/" + getCookie(COOKIE_USER_ID),
        "DELETE",
        {
            password
        },
        204
    );
}

export function updateUserPassword(oldPassword, newPassword) {
    return sendHTTPRequest(
        api + "update/" + getCookie(COOKIE_USER_ID),
        "PUT",
        {
            oldPassword,
            newPassword
        },
        204
    );
}
