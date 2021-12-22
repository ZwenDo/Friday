import {loginUser} from "./login_repository";
import {booleanHTTPRequest} from "../utils/http_requests";
import {getCookie} from "../utils/cookies";

const api = "/api/users/";

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
        api + "delete/" + getCookie("friday-userId"),
        "DELETE",
        {
            password
        },
        204
    );
}

export function updateUserPassword(oldPassword, newPassword) {
    return booleanHTTPRequest(
        api + "update/" + getCookie("friday-userId"),
        "PUT",
        {
            oldPassword,
            newPassword
        },
        204
    );
}