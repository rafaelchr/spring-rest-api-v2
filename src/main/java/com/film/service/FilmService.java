package com.film.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.film.Entity.Film;
import com.film.Repository.FilmRepository;
import com.film.model.CreateFilmRequest;
import com.film.model.FilmResponse;
import com.film.model.PagedResponse;
import com.film.security.CustomUserDetails;

import jakarta.transaction.Transactional;

@Service
public class FilmService {
  @Autowired
  private FilmRepository filmRepository;

  @Autowired
  ValidationService validationService;

  @Transactional
  public PagedResponse<FilmResponse> getFilms(Pageable pageable) {
    Page<Film> page = filmRepository.findAll(pageable);

    List<FilmResponse> films = page.getContent()
        .stream()
        .map(film -> new FilmResponse(
            film.getId(),
            film.getTitle(),
            film.getDescription(),
            film.getCreatedBy().getId(),
            film.getCreatedBy().getUsername()))
        .toList();

    return new PagedResponse<>(
        films,
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages());
  }

  @Transactional
  public FilmResponse getFilmById(Long id) {
    Film film = filmRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found"));
    return FilmResponse
        .builder()
        .id(film.getId())
        .title(film.getTitle())
        .description(film.getDescription())
        .userId(film.getCreatedBy().getId())
        .username(film.getCreatedBy().getUsername())
        .build();
  }

  @Transactional
  public FilmResponse create(CreateFilmRequest request) {
    validationService.validate(request);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

    Film film = new Film();
    film.setTitle(request.getTitle());
    film.setDescription(request.getDescription());
    film.setCreatedBy(userDetails.getUser());

    filmRepository.save(film);

    return new FilmResponse(
        film.getId(),
        film.getTitle(),
        film.getDescription(),
        film.getCreatedBy().getId(),
        film.getCreatedBy().getUsername());
  }
}