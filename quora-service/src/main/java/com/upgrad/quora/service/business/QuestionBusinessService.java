package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserAuthTokenDao;
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
    private QuestionDao questionDao;

    @Autowired
    private UserAuthTokenDao userAuthTokenDao;

    @Autowired
    private UserDao userDao;

    public UserAuthTokenEntity getUserAuthToken(final String authorizationToken) {
        if (authorizationToken != null && !authorizationToken.isEmpty()) {
            String[] bearer = authorizationToken.split("Bearer ");
            if (bearer != null && bearer.length > 1) {
                return userAuthTokenDao.getAuthToken(bearer[1]);
            }
            else {
                return null;
            }
        }
        else{
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

    public List<QuestionEntity> getAllQuestions() {
        return questionDao.getAllQuestions();
    }

    public QuestionEntity getUserForQuestionId(String uuid) {
        return questionDao.getUserForQuestionId(uuid);
    }

     public boolean isUserQuestionOwner(UserEntity user, UserEntity questionOwner) {
        boolean isUserQuestionOwner = false;
        if (user != null && questionOwner != null && user.getUuid() != null && !user.getUuid().isEmpty()
                && questionOwner.getUuid() != null && !questionOwner.getUuid().isEmpty()) {
            if (user.getUuid().equals(questionOwner.getUuid())) {
                isUserQuestionOwner = true;
            }
        }
        return isUserQuestionOwner;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateQuestion(QuestionEntity questionEntity) {
        questionDao.updateQuestion(questionEntity);
    }
    public boolean isUserAdmin(UserEntity user) {
        boolean isUserAdmin = false;
        if (user != null && "admin".equals(user.getRole())) {
            isUserAdmin = true;
        }
        return isUserAdmin;
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteQuestion(QuestionEntity questionEntity) {
        questionDao.deleteQuestion(questionEntity);
    }

    public UserEntity getUserForUserId(String userUuid){
        return userDao.getUser(userUuid);
    }

    public List<QuestionEntity> getQuestionsForUserId(Integer userId) {
        return questionDao.getQuestionsForUserId(userId);
    }
}
