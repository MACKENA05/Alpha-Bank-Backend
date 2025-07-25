package com.mackena.Banking_Application_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {

    public String home(){
        return "Welcome to Mackena Banking Application backend";
    }
}
