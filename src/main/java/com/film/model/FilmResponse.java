package com.film.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilmResponse {
  private Long id;
  private String title;
  private String description;
  private Long userId;
  private String username;
}
