package com.upgrad.quora.api.controller;

import com.upgrad.quora.service.business.UserAdminBusinessService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class AdminController {

    @Autowired
    private UserAdminBusinessService userAdminBusinessService;

    @RequestMapping(method = RequestMethod.DELETE, path = "/admin/user/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void deleteUser(@PathVariable("userId") final String userUuid, @RequestHeader("authorization") final String authorization) {
        userAdminBusinessService.deleteUser(userUuid, authorization);
    }
}
