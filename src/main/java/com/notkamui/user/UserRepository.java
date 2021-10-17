package com.notkamui.user;

public interface UserRepository {
    User save(String username, String password);
}
