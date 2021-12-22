import {loginUser} from "./login_repository";
import {booleanHTTPRequest} from "../utils/http_requests";

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
