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
public class UserAdminBusinessService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteUser(final String userUuid, final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getAuthToken(authorization);
        if(userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        else {
            ZonedDateTime logoutAt = userAuthTokenEntity.getLogoutAt();
            if(logoutAt != null) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out");
            }
            else {
                String role = userAuthTokenEntity.getUser().getRole();
                if(role.equals("admin")) {
                    return userDao.deleteUser(userUuid);
                }
                else {
                    throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
                }
            }
        }
    }
}
