import {setCookie} from "../utils/cookies";

export function loginUser(username, password) {
    let ok = false;
    fetch("/api/auth/login", {
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
            throw new Error("Login failed");
        }
    }).then((response) => {
        setCookie("friday-userId", response['userId']);
        setCookie("friday-userToken", response['token']);
        ok = true;
    }).catch((_) => {
        ok = false;
    });
    return ok;
}

export function registerUser(username, password) {
    let ok = false;
    fetch("/api/user", {
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