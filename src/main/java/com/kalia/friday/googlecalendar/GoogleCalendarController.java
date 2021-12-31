package com.kalia.friday.googlecalendar;

import com.kalia.friday.event.EventRepository;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.inject.Inject;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;

/**
 * API endpoint for communicating with the Google API side of the database.
 */
@ExecuteOn(value = TaskExecutors.IO)
@Controller("/api/googlecalendar")
public class GoogleCalendarController {
    @Inject
    private EventRepository eventRepository;

    @Inject
    private GoogleCalendar googleCalendar;

    /**
     * Imports a Google calendar into a user calendar.
     *
     * @param gCalImportDTO {
     *                      "userId": "",
     *                      "token": "",
     *                      "googleId": ""
     *                      }
     * @return Unauthorized if credentials are invalid ; ServerError if exception is thrown ; empty Ok if events have
     * been correctly imported | redirection if Authentication is required
     */
    @Post
    public HttpResponse<String> importCalendar(@Body @Valid GCalImportDTO gCalImportDTO) {
        try {
            // get events
            var response = googleCalendar.getCalendar(gCalImportDTO.googleId());
            if (response.status() == GoogleCalendar.GoogleCalendarRequestResponse.Status.AUTH_REQUIRED) { // redirect to auth if not authorized
                return HttpResponse.ok(response.url());
            }

            var fridayEvents = GoogleCalendars.toFridayEventList(response.events(), gCalImportDTO.userId(), gCalImportDTO.token());
            eventRepository.authenticatedEventListSave(fridayEvents);
            return HttpResponse.ok(null);
        } catch (IOException | GeneralSecurityException e) {
            return HttpResponse.serverError();
        }
    }
}
