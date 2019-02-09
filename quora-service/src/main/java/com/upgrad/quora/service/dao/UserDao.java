package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.UserNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;
    
    public String deleteUser(final String userUuid) throws UserNotFoundException {

        UserEntity userEntity;
        try {
            userEntity = entityManager.createNamedQuery("userByUuid", UserEntity.class).setParameter("uuid", userUuid).getSingleResult();
        }
        catch(NoResultException nre) {
            userEntity = null;
        }

        if(userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        }
        else {
            entityManager.remove(userEntity);
            return userUuid;
        }




    }

    //Method to retrieve User details if the requesting user is authorized and logged in
    public UserEntity viewUserProfile(final String userUuid) {

        try {

            return entityManager.createNamedQuery("userByUuid", UserEntity.class).setParameter("uuid", userUuid).getSingleResult();

        }
        catch(NoResultException nre) {  //If the user with supplied Uuid is not existing return null

            return null;

        }

    }

    //Method to retrieve UserAuth Entity based on the access token provided in the request header
    public UserAuthTokenEntity getUserAuthToken(final String accesstoken) {

        try {

            return entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthTokenEntity.class).setParameter("accessToken", accesstoken).getSingleResult();

        }
        catch (NoResultException nre) { //If the userAuth entity with supplied access token is not existing return null

            return null;

        }
    }
}
