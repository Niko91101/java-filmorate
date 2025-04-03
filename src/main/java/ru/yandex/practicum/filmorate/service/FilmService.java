package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;

    private final UserStorage userStorage;
    private final LikeStorage likeStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    public Film add(Film film) {
        // Проверка существования MPA
        if (film.getMpa() == null || mpaStorage.findById(film.getMpa().getId()).isEmpty()) {
            throw new NotFoundException("MPA с id=" + (film.getMpa() != null ? film.getMpa().getId() : null) + " не найден");
        }

        // Проверка жанров
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                if (genreStorage.findById(genre.getId()).isEmpty()) {
                    throw new NotFoundException("Жанр с id=" + genre.getId() + " не найден");
                }
            }
        }

        return filmStorage.add(film);
    }

    public Film update(Film film) {
        getById(film.getId());
        validateFilm(film);
        return filmStorage.update(film);
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film getById(Long id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
    }

    public void addLike(Long filmId, Long userId) {
        checkFilmAndUserExist(filmId, userId);
        likeStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        checkFilmAndUserExist(filmId, userId);
        likeStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopular(int count) {
        return likeStorage.getTopLikedFilms(count);
    }

    public List<Film> getMostPopular(int count) {
        return filmStorage.getMostPopular(count);
    }

    private void checkFilmAndUserExist(Long filmId, Long userId) {
        if (filmStorage.getById(filmId).isEmpty()) {
            throw new NotFoundException("Фильм с id=" + filmId + " не найден");
        }
        if (userStorage.getById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
    }

    private void validateFilm(Film film) {
        if (film.getMpa() == null || mpaStorage.findById(film.getMpa().getId()).isEmpty()) {
            throw new NotFoundException("Рейтинг MPA с id=" + film.getMpa().getId() + " не найден");
        }

        for (Genre genre : film.getGenres()) {
            if (genreStorage.findById(genre.getId()).isEmpty()) {
                throw new NotFoundException("Жанр с id=" + genre.getId() + " не найден");
            }
        }
    }
}
