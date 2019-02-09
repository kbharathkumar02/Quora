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

    /**
     * This method creates question
     * @param questionRequest
     * @param authorizationToken
     * @return
     * @throws AuthorizationFailedException
     */
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

    /**
     * This methhod retrieves all the question
     * @param authorizationToken
     * @return
     * @throws AuthorizationFailedException
     */
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

    /**
     * This method edits the question content for the question id
     * @param authorizationToken
     * @param questionId
     * @param questionEditRequest
     * @return
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */

    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent(@RequestHeader("authorization") final String authorizationToken,
                                                                    @PathVariable("questionId") final String questionId, QuestionEditRequest questionEditRequest)
            throws AuthorizationFailedException, InvalidQuestionException {
        QuestionEditResponse questionEditResponse = new QuestionEditResponse();

        UserAuthTokenEntity userAuthTokenEntity = questionBusinessService.getUserAuthToken(authorizationToken);
        if (userAuthTokenEntity != null) {
            if (questionBusinessService.isUserSignedIn(userAuthTokenEntity)) {
                QuestionEntity questionEntity = questionBusinessService.getUserForQuestionId(questionId);
                if (questionEntity != null) {
                    if (questionBusinessService.isUserQuestionOwner(userAuthTokenEntity.getUser(), questionEntity.getUser())) {
                        questionEntity.setContent(questionEditRequest.getContent());
                        questionBusinessService.updateQuestion(questionEntity);
                        questionEditResponse.setId(questionEntity.getUuid());
                        questionEditResponse.setStatus(QUESTION_EDITED);
                    } else {
                        throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
                    }
                } else {
                    throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
                }
            } else {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
            }
        } else {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }

    /**
     * This method deletes the question for a given question id
     * @param authorizationToken
     * @param questionId
     * @return
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@RequestHeader("authorization") final String authorizationToken,
                                                                 @PathVariable("questionId") final String questionId)
            throws AuthorizationFailedException, InvalidQuestionException {
        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse();
        UserAuthTokenEntity userAuthTokenEntity = questionBusinessService.getUserAuthToken(authorizationToken);
        if (userAuthTokenEntity != null) {
            if (questionBusinessService.isUserSignedIn(userAuthTokenEntity)) {
                QuestionEntity questionEntity = questionBusinessService.getUserForQuestionId(questionId);
                if (questionEntity != null) {
                    if (questionBusinessService.isUserQuestionOwner(userAuthTokenEntity.getUser(), questionEntity.getUser())
                            || questionBusinessService.isUserAdmin(userAuthTokenEntity.getUser())) {
                        questionBusinessService.deleteQuestion(questionEntity);
                        questionDeleteResponse.id(questionId).status(QUESTION_DELETED);
                    } else {
                        throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
                    }
                } else {
                    throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
                }
            } else {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
            }
        } else {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }

    /**
     * This method retrieves all the questions by a user
     * @param authorizationToken
     * @param userUuId
     * @return
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     */
    @RequestMapping(method = RequestMethod.GET, path = "/question/all/{userId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(@RequestHeader("authorizationToken") final String authorizationToken,
                                                                               @PathVariable("userId") final String userUuId) throws AuthorizationFailedException,
            UserNotFoundException {
        List<QuestionDetailsResponse> questionDetailsResponseList = new ArrayList<QuestionDetailsResponse>();
        UserEntity userEntity = questionBusinessService.getUserForUserId(userUuId);
        if (userEntity != null) {
            UserAuthTokenEntity userAuthTokenEntity = questionBusinessService.getUserAuthToken(authorizationToken);
            if (userAuthTokenEntity != null) {
                if (questionBusinessService.isUserSignedIn(userAuthTokenEntity)) {

                    if (userEntity != null) {
                        List<QuestionEntity> questionEntityList = questionBusinessService.getQuestionsForUserId(userEntity.getId());
                        if (questionEntityList != null && !questionEntityList.isEmpty()) {
                            for (QuestionEntity questionEntity : questionEntityList) {
                                questionDetailsResponseList.add(new QuestionDetailsResponse().id(questionEntity.getUuid())
                                        .content(questionEntity.getContent()));
                            }
                        } else {
                            System.err.println("0809 not throwing any exception");
                        }
                    }
                } else {

                    throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
                }
            } else {
                throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
            }

        } else {

            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");

        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponseList, HttpStatus.OK);

    }

}
