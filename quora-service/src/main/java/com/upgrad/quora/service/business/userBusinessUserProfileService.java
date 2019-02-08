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
public class userBusinessUserProfileService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity viewUserProfile(final String userUuid,  final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);

        if(userAuthTokenEntity == null) {

            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        else {

            ZonedDateTime logoutAt = userAuthTokenEntity.getLogoutAt();

            if(logoutAt != null) {

                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");

            }
            else {

                UserEntity userEntity = userDao.viewUserProfile(userUuid);
                if(userEntity == null) {
                    throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
                }
                else {
                    return userEntity;
                }

            }

        }

    }
}

