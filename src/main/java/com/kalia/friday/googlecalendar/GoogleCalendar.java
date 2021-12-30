package com.kalia.friday.googlecalendar;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import jakarta.inject.Singleton;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static java.util.Objects.requireNonNull;

/**
 * Class that send requests to Google using Google calendar API.
 */
@Singleton
public class GoogleCalendar {
    private static final String APPLICATION_NAME = "Friday";
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    private final List<String> scopes = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
    private final Path credentialDirectoryPath = Path.of("resources", "credentials.json");

    /**
     * Tries to get a list of Google Calendar events. If an authentication is required, return the url to the
     * authentication page instead.
     *
     * @param userId the Google user id (e.g. foo.example@gmail.com)
     * @return the {@link GoogleCalendarRequestResponse} corresponding to the request.
     */
    public GoogleCalendarRequestResponse getCalendar(String userId) throws IOException, GeneralSecurityException {
        requireNonNull(userId);
        var httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        var flow = createFlow(httpTransport);
        var credential = getCredentials(flow, userId);
        if (credential != null) {
            var data = getCalendarData(credential, httpTransport);
            return new GoogleCalendarRequestResponse(data);
        }
        return new GoogleCalendarRequestResponse(createAuthURI(flow));
    }

    private GoogleAuthorizationCodeFlow createFlow(NetHttpTransport httpTransport) throws IOException {
        requireNonNull(httpTransport);
        try (var in = Files.newInputStream(credentialDirectoryPath)) {
            var clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));
            return new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        }
    }

    private Credential getCredentials(GoogleAuthorizationCodeFlow flow, String userId) throws IOException {
        var credential = flow.loadCredential(userId);
        if (credential != null
            && (credential.getRefreshToken() != null
            || credential.getExpiresInSeconds() == null
            || credential.getExpiresInSeconds() > 60)) {
            return credential;
        }
        return null;
    }

    private String createAuthURI(GoogleAuthorizationCodeFlow flow) {
        return flow.newAuthorizationUrl()
            .setRedirectUri("localhost:8080")
            .build();
    }

    private List<Event> getCalendarData(Credential credential, NetHttpTransport httpTransport) throws IOException {
        var service = new Calendar.Builder(httpTransport, jsonFactory, credential)
            .setApplicationName(APPLICATION_NAME)
            .build();
        return service.events()
            .list("primary")
            .execute()
            .getItems();
    }

    /**
     * Represents a Google calendar request response, containing either the list of events associated to the request or
     * a redirection to an authentication page.
     */
    public static final class GoogleCalendarRequestResponse {
        private final Status status;
        private final String url;
        private final List<Event> events;

        private GoogleCalendarRequestResponse(String url) {
            this.status = Status.AUTH_REQUIRED;
            this.url = url;
            this.events = null;
        }

        private GoogleCalendarRequestResponse(List<Event> events) {
            this.status = Status.OK;
            this.events = List.copyOf(events);
            this.url = null;
        }

        /**
         * Gets the status of the response.
         *
         * @return OK if contains events | AUTH_REQUIRED if contains redirection
         */
        public Status status() {
            return status;
        }

        /**
         * Gets the redirection url.
         *
         * @return the redirection url
         */
        public String url() {
            if (url == null) throw new NoSuchElementException("No url.");
            return url;
        }

        /**
         * Get the event list.
         *
         * @return the event list.
         */
        public List<Event> events() {
            if (events == null) throw new NoSuchElementException("No events.");
            return events;
        }

        /**
         * Status of a {@link GoogleCalendarRequestResponse} response.
         */
        public enum Status {
            AUTH_REQUIRED, OK
        }
    }
}
