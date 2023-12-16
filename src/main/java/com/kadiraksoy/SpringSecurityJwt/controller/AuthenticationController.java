package com.kadiraksoy.SpringSecurityJwt.controller;

import com.kadiraksoy.SpringSecurityJwt.dto.JwtAuthenticationResponse;
import com.kadiraksoy.SpringSecurityJwt.dto.SignInRequest;
import com.kadiraksoy.SpringSecurityJwt.dto.SignUpRequest;
import com.kadiraksoy.SpringSecurityJwt.service.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public JwtAuthenticationResponse signup(@RequestBody SignUpRequest request) {
        return authenticationService.signup(request);
    }

    @PostMapping("/signin")
    public JwtAuthenticationResponse signin(@RequestBody SignInRequest request) {
        return authenticationService.signin(request);
    }
}