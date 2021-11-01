package com.kalia.friday.login;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static java.util.Objects.requireNonNull;

/**
 * Singleton manager to check tokens periodically.
 */
@Singleton
public class SessionManagerService {

    private static final long PURGE_DELAY = 10_800_000; // 3h
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
        new Thread(() -> {
            while (true) {
                try {
                    purgeExpiredTokens();
                    Thread.sleep(PURGE_DELAY);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }).start();
        LOGGER.info("Service started");
    }

    private void purgeExpiredTokens() throws InterruptedException {
        LOGGER.info("Purging outdated tokens");
        var logins = repository.allLogins().get();
        var limit = LocalDateTime.now().minusDays(TOKEN_LIFETIME_IN_DAYS);
        for (var login : logins) {
            if (Thread.interrupted()) throw new InterruptedException();
            if (login.lastRefresh().isBefore(limit)) {
                repository.logout(login.token());
            }
        }
    }
}
