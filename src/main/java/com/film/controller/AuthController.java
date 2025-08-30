package com.film.controller;

import org.springframework.web.bind.annotation.RestController;

import com.film.Entity.User;
import com.film.Repository.UserRepository;
import com.film.model.JwtResponse;
import com.film.model.LoginUserRequest;
import com.film.model.RegisterUserRequest;
import com.film.model.WebResponse;
import com.film.security.JwtUtil;
import com.film.service.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private PasswordEncoder passwordEncoder;
  private AuthService authService;

  public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, AuthService authService,
      PasswordEncoder passwordEncoder) {
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
    this.passwordEncoder = passwordEncoder;
    this.authService = authService;
  }

  @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> register(@RequestBody RegisterUserRequest request) {
    authService.register(request);
    return WebResponse.<String>builder().data("OK").build();
  }

  @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<JwtResponse> login(@RequestBody LoginUserRequest request) {
    JwtResponse response = authService.login(request);
    return WebResponse.<JwtResponse>builder().data(response).build();
  }

  @GetMapping
  public String test() {
    return "success keun nic";
  }
}
