package com.kalia.friday.login;

import com.kalia.friday.DbProperties;
import com.kalia.friday.user.User;
import com.kalia.friday.util.RepositoryResponse;
import com.kalia.friday.util.SHA512Hasher;
import io.micronaut.test.annotation.TransactionMode;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest(transactionMode = TransactionMode.SINGLE_TRANSACTION)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DbProperties
public class LoginRepositoryTest {

    @Inject
    private LoginRepository repository;

    @Inject
    @PersistenceContext
    private EntityManager manager;

    @Inject
    private SHA512Hasher hasher;

    private User user;

    @BeforeEach
    public void setupUser() {
        user = new User(UUID.randomUUID().toString(), hasher.hash("1234"));
        manager.persist(user);
        manager.flush();
        manager.getTransaction().commit();
        manager.getTransaction().begin();
    }

    @AfterEach
    public void clearLogins() {
        manager.createQuery("DELETE FROM Login l").executeUpdate();
    }

    private List<Login> insert10Logins() {
        var logins = new ArrayList<Login>();
        for (int i = 0; i < 10; i++) {
            var login = new Login(user, LocalDateTime.now());
            manager.persist(login);
        }
        manager.flush();
        manager.getTransaction().commit();
        manager.getTransaction().begin();
        return logins;
    }

    private Login insertLogin() {
        var login = new Login(user, LocalDateTime.now());
        manager.persist(login);
        manager.flush();
        manager.getTransaction().commit();
        manager.getTransaction().begin();
        return login;
    }

    @Test
    public void testAllLogins() {
        var logins = insert10Logins();
        var fetched = repository.allLogins().get();
        assertEquals(10, fetched.size());
        assertTrue(() -> {
            var tokens = fetched.stream().map(Login::token).toList();
            for (var login : logins) {
                if (!tokens.contains(login.token())) return false;
            }
            return true;
        });
    }

    @Test
    public void testCheckIdentity() {
        var login = insertLogin();
        var response = repository.checkIdentity(user.id(), login.token());
        assertEquals(RepositoryResponse.Status.OK, response.status());
        assertEquals(login.token(), response.get().token());
    }

    @Test
    public void testCheckIdentityWrongId() {
        var login = insertLogin();
        var response = repository.checkIdentity(UUID.randomUUID(), login.token());
        assertEquals(RepositoryResponse.Status.UNAUTHORIZED, response.status());
    }

    @Test
    public void testCheckIdentityWrongToken() {
        insertLogin();
        var response = repository.checkIdentity(user.id(), UUID.randomUUID());
        assertEquals(RepositoryResponse.Status.UNAUTHORIZED, response.status());
    }

    @Test
    public void testLogin() {
        var response = repository.login(user.username(), "1234");
        assertEquals(RepositoryResponse.Status.OK, response.status());
        var login = response.get();
        var fetched = manager.find(Login.class, new LoginId(login.token(), user));
        assertNotNull(fetched);
        assertEquals(login.token(), fetched.token());
    }

    @Test
    public void testLoginWrongUser() {
        var response = repository.login("wrong", "1234");
        assertEquals(RepositoryResponse.Status.UNAUTHORIZED, response.status());
    }

    @Test
    public void testLoginWrongPassword() {
        var response = repository.login(user.username(), "wrong");
        assertEquals(RepositoryResponse.Status.UNAUTHORIZED, response.status());
    }

    @Test
    public void testLogout() {
        var login = insertLogin();
        var response = repository.logout(login.token());
        assertEquals(RepositoryResponse.Status.OK, response.status());
        var fetched = manager.find(Login.class, new LoginId(login.token(), user));
        assertNull(fetched);
    }

    @Test
    public void testLogoutWrongToken() {
        var response = repository.logout(UUID.randomUUID());
        assertEquals(RepositoryResponse.Status.UNAUTHORIZED, response.status());
    }

    @Test
    public void testLogoutAll() {
        insert10Logins();
        var response = repository.logoutAll(user.id());
        assertEquals(RepositoryResponse.Status.OK, response.status());
        manager.clear();
        var fetched = manager
            .createQuery("SELECT l FROM Login l WHERE l.user = :user", Login.class)
            .setParameter("user", user)
            .getResultList();
        assertEquals(0, fetched.size());
    }

    @Test
    public void testLogoutAllWrongId() {
        insert10Logins();
        var response = repository.logoutAll(UUID.randomUUID());
        assertEquals(RepositoryResponse.Status.UNAUTHORIZED, response.status());
        var fetched = manager
            .createQuery("SELECT l FROM Login l WHERE l.user = :user", Login.class)
            .setParameter("user", user)
            .getResultList();
        assertEquals(10, fetched.size());
    }
}
