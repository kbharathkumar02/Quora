package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserEntity;

import org.springframework.stereotype.Repository;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;
    
    public void deleteUser(final String userUuid) {

        UserEntity userEntity = entityManager.createNamedQuery("userByUuid", UserEntity.class).setParameter("uuid", userUuid).getSingleResult();
        entityManager.remove(userEntity);

    }
}
