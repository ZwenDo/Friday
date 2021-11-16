package com.kalia.friday.user;

import com.kalia.friday.DbProperties;
import com.kalia.friday.util.RepositoryResponse;
import com.kalia.friday.util.SHA512Hasher;
import io.micronaut.test.annotation.TransactionMode;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@MicronautTest(transactionMode = TransactionMode.SINGLE_TRANSACTION)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DbProperties
public class UserRepositoryTest {

    @Inject
    private UserRepository repository;

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

    @Test
    public void testFindById() {
        var fetched = repository.findById(user.id());
        assertEquals(RepositoryResponse.Status.OK, fetched.status());
        assertEquals(user.username(), fetched.get().username());
    }

    @Test
    public void testFindByIdNotFound() {
        var fetched = repository.findById(UUID.randomUUID());
        assertEquals(RepositoryResponse.Status.NOT_FOUND, fetched.status());
    }

    @Test
    public void testFindByUsername() {
        var fetched = repository.findByUsername(user.username());
        assertEquals(RepositoryResponse.Status.OK, fetched.status());
        assertEquals(user.id(), fetched.get().id());
    }

    @Test
    public void testFindByUsernameNotFound() {
        var fetched = repository.findByUsername("not-found");
        assertEquals(RepositoryResponse.Status.NOT_FOUND, fetched.status());
    }

    @Test
    public void testSaveUser() {
        var username = UUID.randomUUID().toString();
        var response = repository.save(username, "1234");
        assertEquals(RepositoryResponse.Status.OK, response.status());
        var saved = response.get();
        var fetched = manager.find(User.class, saved.id());
        assertNotNull(fetched);
        assertEquals(saved.id(), fetched.id());
        assertEquals(saved.username(), fetched.username());
        assertEquals(saved.password(), fetched.password());
        assertEquals(hasher.hash("1234"), fetched.password());
    }

    @Test
    public void testDeleteById() {
        var response = repository.deleteById(user.id(), "1234");
        assertEquals(RepositoryResponse.Status.OK, response.status());
        var fetched = manager.find(User.class, user.id());
        assertNull(fetched);
    }

    @Test
    public void testDeleteByIdNotFound() {
        var response = repository.deleteById(UUID.randomUUID(), "1234");
        assertEquals(RepositoryResponse.Status.NOT_FOUND, response.status());
    }

    @Test
    public void testDeleteByIdUnauthorized() {
        var response = repository.deleteById(user.id(), "12345");
        assertEquals(RepositoryResponse.Status.UNAUTHORIZED, response.status());
    }

    @Test
    public void testUpdatePassword() {
        var response = repository.updatePassword(user.id(), "1234", "abcd");
        assertEquals(RepositoryResponse.Status.OK, response.status());
        var fetched = manager.find(User.class, user.id());
        assertEquals(hasher.hash("abcd"), fetched.password());
    }

    @Test
    public void testUpdatePasswordNotFound() {
        var response = repository.updatePassword(UUID.randomUUID(), "1234", "abcd");
        assertEquals(RepositoryResponse.Status.NOT_FOUND, response.status());
    }

    @Test
    public void testUpdatePasswordUnauthorized() {
        var response = repository.updatePassword(user.id(), "12345", "abcd");
        assertEquals(RepositoryResponse.Status.UNAUTHORIZED, response.status());
    }
}
