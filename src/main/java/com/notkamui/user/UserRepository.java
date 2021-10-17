package com.notkamui.user;

import java.security.NoSuchAlgorithmException;

public interface UserRepository {
    User save(String username, String password) throws NoSuchAlgorithmException;
}
