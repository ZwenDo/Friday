export const COOKIE_USER_ID = 'friday-userId';
export const COOKIE_USER_TOKEN = 'friday-userToken';
export const COOKIE_USER_NAME = 'friday-userName';

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
        .split('; ')
        .find(cookie => cookie.trimStart().startsWith(cName));
    return cookie?.substring(cName.length) ?? undefined;
}

export function deleteCookie(name) {
    document.cookie = `${name}=;max-age=-1;path=/;sameSite=strict`;
}

export function deleteCookies() {
    deleteCookie(COOKIE_USER_ID);
    deleteCookie(COOKIE_USER_NAME);
    deleteCookie(COOKIE_USER_TOKEN);
}
