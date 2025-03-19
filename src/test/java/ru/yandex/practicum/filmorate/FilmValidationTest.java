package ru.yandex.practicum.filmorate;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidationTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldNotAllowEmptyFilmName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);

        Set violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Название фильма не должно быть пустым");
    }

    @Test
    void shouldNotAllowLongDescription() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("A".repeat(201)); // 201 символ
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);

        Set violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Описание не должно превышать 200 символов");
    }

    @Test
    void shouldNotAllowReleaseDateBefore1895() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1890, 3, 25)); // Раньше 28 декабря 1895
        film.setDuration(100);

        Set violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Дата релиза не может быть раньше 28 декабря 1895 года");
    }

    @Test
    void shouldNotAllowNegativeDuration() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-100);

        Set violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Продолжительность фильма должна быть положительной");
    }
}