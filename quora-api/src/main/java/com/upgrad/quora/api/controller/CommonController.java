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
//Controller class for common functionalities
public class CommonController {

    @Autowired
    private userBusinessUserProfileService userBusinessUserProfileService;  //instantiating the class with business logic for view user profile functionality

    //Method to be executed based on the request mapping
    //Taking UserUuid as input through path variable for which the profile should be viewed
    //Authorization token in request header to check if the user is logged in
    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public UserEntity viewUserProfile(@PathVariable("userId") final String userUuid,  @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        return userBusinessUserProfileService.viewUserProfile(userUuid, authorization);
    }
}
