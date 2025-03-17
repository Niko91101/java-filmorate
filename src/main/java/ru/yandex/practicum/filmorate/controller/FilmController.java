package ru.yandex.practicum.filmorate.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private long nextId = 1;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);



    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        film.setId(nextId++); // Генерация нового ID
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;  // Возвращаем просто объект Film
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }
        films.put(film.getId(), film);
        log.info("Обновлен фильм: {}", film);
        return film;  // Возвращаем просто объект Film
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }
}
