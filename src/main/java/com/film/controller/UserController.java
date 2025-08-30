package com.film.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.film.model.PagedResponse;
import com.film.model.UserResponse;
import com.film.model.WebResponse;
import com.film.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/users")
public class UserController {
  @Autowired
  private UserService userService;

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<PagedResponse<UserResponse>> getUsers(Pageable pageable) {
    PagedResponse<UserResponse> response = userService.getUsers(pageable);
    return WebResponse.<PagedResponse<UserResponse>>builder().data(response).build();
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<UserResponse> getUserById(@PathVariable Long id) {
    UserResponse response = userService.getUserById(id);
    return WebResponse.<UserResponse>builder().data(response).build();
  }
}
