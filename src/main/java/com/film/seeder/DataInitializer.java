package com.film.seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.film.Entity.Role;
import com.film.Entity.User;
import com.film.Repository.UserRepository;

@Configuration
public class DataInitializer {
  @Bean
  public CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    return args -> {
      if (userRepository.findByUsername("admin").isEmpty()) {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setRole(Role.ADMIN);

        userRepository.save(admin);
        System.out.println("Admin account created: username=admin, password=admin");
      }
    };
  }
}
