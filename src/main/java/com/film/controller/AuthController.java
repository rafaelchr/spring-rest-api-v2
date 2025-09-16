package com.film.controller;

import org.springframework.web.bind.annotation.RestController;

import com.film.model.JwtResponse;
import com.film.model.LoginResponse;
import com.film.model.LoginUserRequest;
import com.film.model.RegisterUserRequest;
import com.film.model.WebResponse;
import com.film.security.JwtUtil;
import com.film.service.AuthService;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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
  public ResponseEntity<WebResponse<LoginResponse>> login(@RequestBody LoginUserRequest request) {
    JwtResponse jwtResponse = authService.login(request);

    ResponseCookie jwtCookie = ResponseCookie.from("jwt", jwtResponse.getToken())
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(Duration.ofSeconds(60))
        .sameSite("None")
        .build();

    LoginResponse response = LoginResponse.builder()
        .username(jwtResponse.getUsername())
        .role(jwtResponse.getRole())
        .build();

    WebResponse<LoginResponse> webResponse = WebResponse.<LoginResponse>builder().data(response).build();

    return ResponseEntity
        .ok()
        .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
        .body(webResponse);
  }

  @PostMapping("/logout")
  public ResponseEntity<WebResponse<String>> logout() {
    ResponseCookie response = ResponseCookie.from("jwt", "")
        .httpOnly(true)
        .secure(false)
        .path("/")
        .maxAge(0)
        .build();

    WebResponse<String> webResponse = WebResponse.<String>builder().data("OK").build();

    return ResponseEntity
        .ok()
        .header(HttpHeaders.SET_COOKIE, response.toString())
        .body(webResponse);
  }

  @GetMapping
  public String test() {
    return "success keun nic";
  }
}
