package com.film.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.film.model.CreateFilmRequest;
import com.film.model.FilmResponse;
import com.film.model.PagedResponse;
import com.film.model.WebResponse;
import com.film.service.FilmService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/films")
public class FilmController {
  @Autowired
  private FilmService filmService;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<PagedResponse<FilmResponse>> getFilms(Pageable pageable) {
    PagedResponse<FilmResponse> response = filmService.getFilms(pageable);
    return WebResponse.<PagedResponse<FilmResponse>>builder().data(response).build();
  }

  @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<FilmResponse> getFilmById(@PathVariable Long id) {
    FilmResponse response = filmService.getFilmById(id);
    return WebResponse.<FilmResponse>builder().data(response).build();
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<FilmResponse> createFilm(@RequestBody CreateFilmRequest request) {
    FilmResponse response = filmService.create(request);
    return WebResponse.<FilmResponse>builder().data(response).build();
  }
}
