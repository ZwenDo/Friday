package com.kalia.friday.login;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Singleton manager to check tokens periodically.
 */
@Singleton
public class SessionManagerService {

    private static final long PURGE_DELAY = 10_800_000; // 3h
    private static final long TOKEN_LIFETIME_IN_DAYS = 7;
    private final Logger logger = LoggerFactory.getLogger(SessionManagerService.class);

    @Inject
    private LoginRepository repository;

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
        logger.info("Service started");
    }

    private void purgeExpiredTokens() {
        logger.info("Purging outdated tokens");
        repository.purgeExpiredTokens(TOKEN_LIFETIME_IN_DAYS);
    }
}
