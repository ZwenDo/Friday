export function sendHTTPRequest(url, method, body, okCode, onSuccess, onFail=(e) => console.log(e)) {
    fetch(url, {
        method: method,
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json"
        },
        body: JSON.stringify(body)
    }).then(response => {
        if (response.status === okCode) {
            onSuccess(response);
        } else {
            throw new Error();
        }
    }).catch(e => {
        onFail(e)
    });
}
