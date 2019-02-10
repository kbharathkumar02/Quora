package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.mockito.stubbing.Answer;
import org.omg.CORBA.DynAnyPackage.Invalid;
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
public class AnswerController {

    @Autowired
    private AnswerBusinessService answerBusinessService;

    @Autowired
    private QuestionBusinessService questionBusinessService;

    private static String ANSWER_CREATED = "ANSWER_CREATED";
    private static String ANSWER_EDITED = "ANSWER EDITED";
    private static String ANSWER_DELETED = "ANSWER DELETED";


    /**
     * This method creates answer
     * @param answerRequest
     * @param authorizationToken
     * @return
     * @throws AuthorizationFailedException
     */
    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(AnswerRequest answerRequest, @PathVariable("questionId") final String questionId,
                                                       @RequestHeader("authorization") final String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {
        AnswerEntity answerEntity = answerBusinessService.performCreateAnswer(authorizationToken, questionId, answerRequest.getAnswer());
        AnswerResponse answerResponse = new AnswerResponse();
        answerResponse.id(answerEntity.getUuid()).status(ANSWER_CREATED);
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.OK);
    }

}
