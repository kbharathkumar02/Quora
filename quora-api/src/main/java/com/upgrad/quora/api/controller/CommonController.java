package com.upgrad.quora.api.controller;


import com.upgrad.quora.service.business.userBusinessUserProfileService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class CommonController {

    @Autowired
    private userBusinessUserProfileService userBusinessUserProfileService;


    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public UserEntity viewUserProfile(@PathVariable("userId") final String userUuid,  @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        return userBusinessUserProfileService.viewUserProfile(userUuid, authorization);
    }
}
