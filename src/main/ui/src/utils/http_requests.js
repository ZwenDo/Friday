export function booleanHTTPRequest(url, method, body, okCode, callback) {
    let ok = false;
    fetch(url, {
        method: method,
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json"
        },
        body: JSON.stringify(body)
    }).then((response) => {
        if (response.status === okCode) {
            ok = callback(response);
        } else {
            throw new Error("Failed");
        }
    }).catch((_) => {
        ok = false;
    });
    return ok;
}