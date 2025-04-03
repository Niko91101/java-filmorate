package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({GenreDbStorage.class, GenreRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbStorageTest {

    private final GenreDbStorage genreDbStorage;

    @Test
    void shouldReturnAllGenres() {
        List<Genre> genres = genreDbStorage.findAll();

        assertThat(genres)
                .isNotNull()
                .isNotEmpty()
                .allSatisfy(genre ->
                        assertThat(genre)
                                .hasFieldOrProperty("id")
                                .hasFieldOrProperty("name")
                );
    }

    @Test
    void shouldReturnGenreById() {
        Optional<Genre> genre = genreDbStorage.findById(1);

        assertThat(genre)
                .isPresent()
                .hasValueSatisfying(g ->
                        assertThat(g)
                                .hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    void shouldReturnEmptyForInvalidId() {
        Optional<Genre> genre = genreDbStorage.findById(999);

        assertThat(genre).isEmpty();
    }
}
