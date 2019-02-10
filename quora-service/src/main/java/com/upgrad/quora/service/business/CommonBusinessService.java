package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
//Class with business logic to view user profile
public class CommonBusinessService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity viewUserProfile(final String userUuid, final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getAuthToken(authorization);
        if(userAuthTokenEntity == null) {   //If the requesting user is not logged throw a custom exception with the message below
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        else {
            ZonedDateTime logoutAt = userAuthTokenEntity.getLogoutAt();
            if(logoutAt != null) {  //If the requesting user is already logged out by the time view user profile is called throw a custom exception with message below
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
            }
            else {
                UserEntity userEntity = userDao.viewUserProfile(userUuid);
                if(userEntity == null) {    //If the requesting user is logged in but trying to view user profile for non existing user
                    throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
                }
                else {  //If the requesting user is logged in and trying to view user profile for existing user return the user details
                    return userEntity;
                }
            }
        }
    }
}