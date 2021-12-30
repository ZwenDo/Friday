package com.kalia.friday.googlecalendar;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

public final class GoogleCalendarTest {

    @Test
    public void testGoogleCalendarWithUnknownUser() throws GeneralSecurityException, IOException {
        var response = new GoogleCalendar().getCalendar("foo");
        Assertions.assertEquals(GoogleCalendar.GoogleCalendarRequestResponse.Status.AUTH_REQUIRED, response.status());
    }

}
