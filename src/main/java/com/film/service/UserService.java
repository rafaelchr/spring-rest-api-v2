package com.film.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.film.Entity.User;
import com.film.Repository.UserRepository;
import com.film.model.PagedResponse;
import com.film.model.UserResponse;

import jakarta.transaction.Transactional;

@Service
public class UserService {
  @Autowired
  private UserRepository userRepository;

  @Transactional
  public PagedResponse<UserResponse> getUsers(Pageable pageable) {
    Page<User> page = userRepository.findAll(pageable);

    List<UserResponse> users = page.getContent()
        .stream()
        .map(user -> new UserResponse(user.getUsername(), user.getRole())).toList();

    return new PagedResponse<>(
        users,
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages());
  }

  @Transactional
  public UserResponse getUserById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    return UserResponse
        .builder()
        .username(user.getUsername())
        .role(user.getRole())
        .build();
  }
}
