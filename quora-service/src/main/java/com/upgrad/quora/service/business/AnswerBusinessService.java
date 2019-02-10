//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserAuthTokenDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnswerBusinessService {

    @Autowired
    private QuestionBusinessService questionBusinessService;
    @Autowired
    private UserBusinessService userBusinessService;
    @Autowired
    private AnswerDao answerDao;

    public AnswerBusinessService() {
    }

    public AnswerEntity getAnswerForAnswerId(String uuid) {
        return answerDao.getAnswerForAnswerId(uuid);
    }

    public boolean isUserAnswerOwner(UserEntity user, UserEntity answerOwner) {
        boolean isUserAnswerOwner = false;
        if (user != null && answerOwner != null && user.getUuid() != null && !user.getUuid().isEmpty() && answerOwner.getUuid() != null && !answerOwner.getUuid().isEmpty() && user.getUuid().equals(answerOwner.getUuid())) {
            isUserAnswerOwner = true;
            return isUserAnswerOwner;
        }
        return isUserAnswerOwner;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity performCreateAnswer(final String authorizationToken, String questionId, String answerContent) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthTokenEntity = userBusinessService.getUserAuthToken(authorizationToken);
        AnswerEntity answerEntity = new AnswerEntity();
        if (userAuthTokenEntity != null) {
            if (userBusinessService.isUserSignedIn(userAuthTokenEntity)) {
                QuestionEntity question  = questionBusinessService.getQuestionForQuestionId(questionId);
                if (question != null) {
                    answerEntity.setDate(ZonedDateTime.now());
                    answerEntity.setAnswer(answerContent);
                    answerEntity.setUuid(UUID.randomUUID().toString());
                    answerEntity.setUser(userAuthTokenEntity.getUser());
                    answerEntity = answerDao.createAnswer(answerEntity);
                } else {
                    throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
                }
            } else {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
            }
        } else {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        return answerEntity;
    }

}
