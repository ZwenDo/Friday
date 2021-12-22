export function setCookie(name, value, expirationInDays = 7) {
    const d = new Date();
    const days = expirationInDays * 24 * 60 * 60 * 1000;
    d.setTime(d.getTime() + days);
    const expires = `expires=${d.toUTCString()}`;
    document.cookie = `${name}=${value};${expires};path=/;sameSite=strict`;
}

export function getCookie(name) {
    const cName = `${name}=`;
    const cookie = decodeURIComponent(document.cookie)
        .split(';')
        .find(cookie => cookie.trimStart().startsWith(cName))
    return cookie?.substring(cName.length, cookie.length) ?? undefined;
}

export function deleteCookie(name) {
    document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/; sameSite=strict`;
}
