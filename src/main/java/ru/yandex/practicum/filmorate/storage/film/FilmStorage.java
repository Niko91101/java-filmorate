package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film add(Film film);

    Film update(Film film);

    Optional<Film> getById(Long id);

    List<Film> findAll();

    List<Film> getMostPopular(int count);


}
