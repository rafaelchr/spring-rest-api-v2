package com.film.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.film.Entity.Role;
import com.film.Entity.User;
import com.film.Repository.UserRepository;
import com.film.model.JwtResponse;
import com.film.model.LoginUserRequest;
import com.film.model.RegisterUserRequest;
import com.film.security.CustomUserDetails;
import com.film.security.JwtUtil;

import jakarta.transaction.Transactional;

@Service
public class AuthService {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private ValidationService validationService;

  @Transactional
  public void register(RegisterUserRequest request) {
    validationService.validate(request);

    if (userRepository.existsByUsername(request.getUsername())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already registered");
    }

    User user = new User();
    user.setUsername(request.getUsername());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(Role.USER);

    userRepository.save(user);
  }

  @Transactional
  public JwtResponse login(LoginUserRequest request) {
    validationService.validate(request);

    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

      String token = jwtUtil.generateToken(userDetails);

      return JwtResponse.builder().username(userDetails.getUsername()).role(userDetails.getRole()).token(token).build();
    } catch (AuthenticationException e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password wrong");
    }
  }
}
