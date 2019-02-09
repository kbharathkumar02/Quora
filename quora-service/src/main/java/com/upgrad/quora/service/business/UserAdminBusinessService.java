package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
//Class with business logic to delete user
public class UserAdminBusinessService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteUser(final String userUuid, final String authorization) throws AuthorizationFailedException, UserNotFoundException {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);

        if(userAuthTokenEntity == null) {   //If the requesting user is not logged throw a custom exception with the message below

            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");

        }
        else {

            ZonedDateTime logoutAt = userAuthTokenEntity.getLogoutAt();

            if(logoutAt != null) {  ////If the requesting user is already logged out by the time delete user is called throw a custom exception with message below

                throw new AuthorizationFailedException("ATHR-002", "User is signed out");

            }
            else {

                String role = userAuthTokenEntity.getUser().getRole();

                if(role.equals("admin")) {  //If the requesting user who is admin(authorized) and logged in and trying to delete user call this method which deletes the user

                    return userDao.deleteUser(userUuid);

                }
                else {  //If the requesting user who is logged in but is nonadmin/not authorized and trying to delete user throw this exception with the message below

                    throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
                }


            }

        }





    }
}
