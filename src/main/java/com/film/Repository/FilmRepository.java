package com.film.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.film.Entity.Film;

public interface FilmRepository extends JpaRepository<Film, Long> {

}
