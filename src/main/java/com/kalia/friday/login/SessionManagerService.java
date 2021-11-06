package com.kalia.friday.login;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

/**
 * Singleton manager to check tokens periodically.
 */
@Singleton
public class SessionManagerService {

    private static final long PURGE_DELAY = 1000; // 3h
    private static final long TOKEN_LIFETIME_IN_DAYS = 7;
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionManagerService.class);

    @Inject
    private final LoginRepository repository;

    /**
     * Used by Micronaut to create a singleton repository by injection.
     *
     * @param repository the login repository to be injected
     */
    public SessionManagerService(@NotNull LoginRepository repository) {
        this.repository = requireNonNull(repository);
    }

    /**
     * Starts the service and periodically checks for expired tokens.
     */
    public void start() {
        Executors
            .newSingleThreadScheduledExecutor((r) -> new Thread(r, "Session Manager"))
            .scheduleAtFixedRate(
                this::purgeExpiredTokens,
                0, PURGE_DELAY, TimeUnit.MILLISECONDS
            );
        LOGGER.info("Service started");
    }

    private void purgeExpiredTokens() {
        LOGGER.info("Purging outdated tokens");
        repository.purgeExpiredTokens(TOKEN_LIFETIME_IN_DAYS);
    }
}
