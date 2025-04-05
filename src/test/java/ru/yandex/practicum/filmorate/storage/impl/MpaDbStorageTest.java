package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mapper.MpaRowMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({MpaDbStorage.class, MpaRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {

    private final MpaDbStorage mpaDbStorage;

    @Test
    void shouldReturnAllMpaRatings() {
        List<Mpa> ratings = mpaDbStorage.findAll();

        assertThat(ratings)
                .isNotNull()
                .isNotEmpty()
                .allSatisfy(mpa ->
                        assertThat(mpa)
                                .hasFieldOrProperty("id")
                                .hasFieldOrProperty("name")
                );
    }

    @Test
    void shouldReturnMpaById() {
        Optional<Mpa> mpa = mpaDbStorage.findById(1);

        assertThat(mpa)
                .isPresent()
                .hasValueSatisfying(rating ->
                        assertThat(rating)
                                .hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    void shouldReturnEmptyForInvalidId() {
        Optional<Mpa> mpa = mpaDbStorage.findById(999);

        assertThat(mpa).isEmpty();
    }
}

