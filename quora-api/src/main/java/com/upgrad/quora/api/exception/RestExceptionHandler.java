package com.upgrad.quora.api.exception;

import com.upgrad.quora.api.model.ErrorResponse;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundException(UserNotFoundException exc, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<ErrorResponse> unauthorizedException(AuthorizationFailedException
                                                                       exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(InvalidQuestionException.class)
    public ResponseEntity<ErrorResponse> invalidQuestionException(InvalidQuestionException
                                                                          exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(AnswerNotFoundException.class)
    public ResponseEntity<ErrorResponse> answerNotFoundException(AnswerNotFoundException
                                                                          exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.NOT_FOUND
        );
    }
}
