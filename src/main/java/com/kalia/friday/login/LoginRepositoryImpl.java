package com.kalia.friday.login;

import com.kalia.friday.user.User;
import com.kalia.friday.util.RepositoryResponse;
import com.kalia.friday.util.SHA512Hasher;
import jakarta.inject.Singleton;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@Singleton
public class LoginRepositoryImpl implements LoginRepository {

    private final EntityManager manager;
    private final SHA512Hasher hasher = SHA512Hasher.getHasher();

    public LoginRepositoryImpl(EntityManager manager) throws NoSuchAlgorithmException {
        this.manager = requireNonNull(manager);
    }

    @Override
    @Transactional
    public RepositoryResponse<Login> checkIdentity(UUID user, UUID token) {
        var result = manager
            .createQuery("SELECT l FROM Login l WHERE l.token = :token AND l.user = :user", Login.class)
            .setParameter("token", token)
            .setParameter("user", user)
            .getResultList();
        if (result.isEmpty()) return RepositoryResponse.unauthorized();
        var login = result.get(0);
        login.setLastRefresh(new Date());
        manager.merge(login);
        return RepositoryResponse.ok(login);
    }

    @Override
    @Transactional
    public RepositoryResponse<Login> login(String username, String password) {
        var result = manager
            .createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
            .setParameter("username", username)
            .getResultList();
        if (result.isEmpty()) return RepositoryResponse.notFound();
        var user = result.get(0);
        if (!hasher.hash(password).equals(user.password())) return RepositoryResponse.unauthorized();
        var login = new Login(result.get(0), new Date());
        manager.persist(login);
        return RepositoryResponse.ok(login);
    }

    @Override
    @Transactional
    public RepositoryResponse<Login> logout(UUID token) {
        var result = manager
            .createQuery("SELECT l FROM Login l WHERE l.token = :token", Login.class)
            .setParameter("token", token)
            .getResultList();
        if (result.isEmpty()) return RepositoryResponse.notFound();
        var login = result.get(0);
        manager.remove(login);
        return RepositoryResponse.ok(login);
    }
}
