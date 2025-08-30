package com.film.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.film.Entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  Boolean existsByUsername(String username);
}
