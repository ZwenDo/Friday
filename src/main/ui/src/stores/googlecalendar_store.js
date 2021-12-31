import {sendHTTPRequest} from "../utils/http_requests";
import {COOKIE_USER_ID, COOKIE_USER_TOKEN, getCookie} from "../utils/cookies";

const api = "/api/googlecalendar/";

export function importFromGoogleCalendar(googleId, onSuccess) {
    sendHTTPRequest(
        api,
        "POST",
        {
            googleId: googleId,
            userId: getCookie(COOKIE_USER_ID),
            token: getCookie(COOKIE_USER_TOKEN)
        },
        200,
        r => {
            r.text().then(data => {
                if (data) {
                    const win = window.open(data, '_self');
                    win.focus();
                } else {
                    onSuccess();
                }
            });
        },
        _ => alert("Failed to import from Google Calendar")
    );
}
