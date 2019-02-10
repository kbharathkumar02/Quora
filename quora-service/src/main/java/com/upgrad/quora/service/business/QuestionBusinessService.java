package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class QuestionBusinessService {

    @Autowired
    private UserBusinessService userBusinessService;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;


    public UserAuthTokenEntity getUserAuthToken(final String authorizationToken) {
        if (authorizationToken != null && !authorizationToken.isEmpty() && authorizationToken.split("Bearer").length > 1) {
            String bearerToken = authorizationToken.split("Bearer ")[1];
            return userDao.getAuthToken(bearerToken);
        } else {
            return null;
        }
    }


    public boolean isUserSignedIn(UserAuthTokenEntity userAuthTokenEntity) {
        boolean isUserSignedIn = false;
        if (userAuthTokenEntity != null) {
            if (userAuthTokenEntity.getExpiresAt() != null && ZonedDateTime.now().isBefore(userAuthTokenEntity.getExpiresAt())) {
                isUserSignedIn = true;
            }
        }
        return isUserSignedIn;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        return questionDao.createQuestion(questionEntity);
    }

}
