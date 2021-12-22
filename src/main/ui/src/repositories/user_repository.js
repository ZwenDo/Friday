import {loginUser} from "./login_repository";

const api = "/api/users/";

export function registerUser(username, password) {
    let ok = false;
    fetch(api + "save", {
        method: "POST",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            username: username,
            password: password
        })
    }).then((response) => {
        if (response.status === 201) {
            return response.json();
        } else {
            throw new Error("Registration failed");
        }
    }).then((_) => {
        ok = loginUser(username, password);
    }).catch((_) => {
        ok = false;
    });
    return ok;
}
