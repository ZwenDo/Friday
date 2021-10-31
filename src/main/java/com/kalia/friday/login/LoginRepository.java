package com.kalia.friday.login;

import com.kalia.friday.util.RepositoryResponse;

import javax.transaction.Transactional;
import java.util.UUID;

public interface LoginRepository {

    @Transactional
    RepositoryResponse<Login> checkIdentity(UUID user, UUID token);

    @Transactional
    RepositoryResponse<Login> login(String username, String password);

    @Transactional
    RepositoryResponse<Login> logout(UUID token);
}
