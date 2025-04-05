package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.impl.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.MpaRowMapper;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({
        FilmDbStorage.class,
        GenreDbStorage.class,
        MpaDbStorage.class,
        FilmMapper.class,
        GenreRowMapper.class,
        MpaRowMapper.class
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final MpaDbStorage mpaDbStorage;

    @Test
    void shouldAddAndFindFilmById() {
        Mpa mpa = mpaDbStorage.findById(1).get();


        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(mpa);

        Film addedFilm = filmDbStorage.add(film);
        Optional<Film> filmOptional = filmDbStorage.getById(addedFilm.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f)
                                .hasFieldOrPropertyWithValue("id", addedFilm.getId())
                                .hasFieldOrPropertyWithValue("name", "Test Film")
                );
    }

    @Test
    void shouldUpdateFilm() {
        Mpa mpa = mpaDbStorage.findById(1).get();

        Film film = new Film();
        film.setName("Original Film");
        film.setDescription("Original description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(mpa);

        Film addedFilm = filmDbStorage.add(film);

        // Обновляем
        addedFilm.setName("Updated Film");
        addedFilm.setDescription("Updated description");
        addedFilm.setDuration(150);
        filmDbStorage.update(addedFilm);

        Optional<Film> updatedFilm = filmDbStorage.getById(addedFilm.getId());

        assertThat(updatedFilm)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f)
                                .hasFieldOrPropertyWithValue("name", "Updated Film")
                                .hasFieldOrPropertyWithValue("description", "Updated description")
                                .hasFieldOrPropertyWithValue("duration", 150)
                );
    }

    @Test
    void shouldReturnAllFilms() {
        Mpa mpa = mpaDbStorage.findById(1).get();

        Film film1 = new Film();
        film1.setName("Film One");
        film1.setDescription("First");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(100);
        film1.setMpa(mpa);
        filmDbStorage.add(film1);

        Film film2 = new Film();
        film2.setName("Film Two");
        film2.setDescription("Second");
        film2.setReleaseDate(LocalDate.of(2002, 2, 2));
        film2.setDuration(110);
        film2.setMpa(mpa);
        filmDbStorage.add(film2);

        List<Film> films = filmDbStorage.findAll();

        assertThat(films)
                .isNotNull()
                .hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void shouldReturnMostPopularFilms() {
        Mpa mpa = mpaDbStorage.findById(1).get();

        Film film1 = new Film();
        film1.setName("Popular One");
        film1.setDescription("Desc 1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(100);
        film1.setMpa(mpa);
        filmDbStorage.add(film1);

        Film film2 = new Film();
        film2.setName("Popular Two");
        film2.setDescription("Desc 2");
        film2.setReleaseDate(LocalDate.of(2000, 2, 2));
        film2.setDuration(120);
        film2.setMpa(mpa);
        filmDbStorage.add(film2);

        List<Film> popularFilms = filmDbStorage.getMostPopular(10);

        assertThat(popularFilms)
                .isNotNull()
                .isNotEmpty()
                .extracting(Film::getName)
                .contains("Popular One", "Popular Two");
    }

}
