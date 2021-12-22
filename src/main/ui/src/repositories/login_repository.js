import {deleteCookie, getCookie, setCookie} from "../utils/cookies";

const api = "/api/auth/";

export function loginUser(username, password) {
    let ok = false;
    fetch(api + "login", {
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

function logout(all = false) {
    let ok = false;
    fetch(api + "logout" + (all ? "/all" : ""), {
        method: "POST",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            userId: getCookie("friday-userId"),
            token: getCookie("friday-userToken")
        })
    }).then((response) => {
        if (response.status === 202) {
            ok = true;
            deleteCookie("friday-userId");
            deleteCookie("friday-userToken");
        } else {
            throw new Error("Logout failed");
        }
    }).catch((_) => {
        ok = false;
    });
    return ok;
}

export function logoutUser() {
    return logout();
}

export function logoutAll() {
    return logout(true);
}