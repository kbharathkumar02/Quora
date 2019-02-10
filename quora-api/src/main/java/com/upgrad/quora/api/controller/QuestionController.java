package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionBusinessService questionBusinessService;

    private static String QUESTION_CREATED = "QUESTION_CREATED";
    private static String QUESTION_EDITED = "QUESTION EDITED";
    private static String QUESTION_DELETED = "QUESTION DELETED";

    @RequestMapping(method = RequestMethod.POST, path = "/question/create",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(QuestionRequest questionRequest,
                                                           @RequestHeader("authorization") final String authorizationToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = questionBusinessService.getUserAuthToken(authorizationToken);
        QuestionEntity questionEntity = new QuestionEntity();
        QuestionResponse questionResponse = new QuestionResponse();
        if (userAuthTokenEntity != null) {
            if (questionBusinessService.isUserSignedIn(userAuthTokenEntity)) {
                questionEntity.setDate(ZonedDateTime.now());
                questionEntity.setContent(questionRequest.getContent());
                questionEntity.setUuid(UUID.randomUUID().toString());
                questionEntity.setUser(userAuthTokenEntity.getUser());
                final QuestionEntity createdQuestionEntity = questionBusinessService.createQuestion(questionEntity);
            } else {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
            }
        } else {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        questionResponse.id(questionEntity.getUuid()).status(QUESTION_CREATED);
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/question/all",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authorizationToken) throws AuthorizationFailedException {
        List<QuestionDetailsResponse> questionDetailsResponseList = new ArrayList<QuestionDetailsResponse>();
        UserAuthTokenEntity userAuthTokenEntity = questionBusinessService.getUserAuthToken(authorizationToken);
        if (userAuthTokenEntity != null) {
            if (questionBusinessService.isUserSignedIn(userAuthTokenEntity)) {
                List<QuestionEntity> questionEntityList = new ArrayList<QuestionEntity>();
                questionEntityList = questionBusinessService.getAllQuestions();
                if (questionEntityList != null && !questionEntityList.isEmpty()) {
                    for (QuestionEntity qEntity : questionEntityList) {
                        questionDetailsResponseList.add(new QuestionDetailsResponse().id(qEntity.getUuid()).content(qEntity.getContent()));
                    }
                }

            } else {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
            }
        } else {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponseList, HttpStatus.OK);

    }

}
