package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAdminBusinessService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(final String userUuid, final String authorization) {

        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorization);

        String role = userAuthTokenEntity.getUser().getRole();

        if(role.equals("admin")) {

            userDao.deleteUser(userUuid);

        }

    }
}
