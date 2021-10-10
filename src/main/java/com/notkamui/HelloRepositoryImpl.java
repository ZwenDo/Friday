package com.notkamui;

import com.notkamui.domain.HelloDTO;
import jakarta.inject.Singleton;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@Singleton
public class HelloRepositoryImpl implements HelloRepository {

    private final EntityManager entityManager;

    public HelloRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public HelloDTO save() {
        var hello = new HelloDTO();
        entityManager.persist(hello);
        return hello;
    }
}
