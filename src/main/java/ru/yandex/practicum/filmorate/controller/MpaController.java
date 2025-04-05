package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.dao.MpaStorage;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    private final MpaStorage mpaStorage;

    @GetMapping
    public List<Mpa> getAll() {
        return mpaStorage.findAll();
    }

    @GetMapping("/{id}")
    public Mpa getById(@PathVariable("id") int id) {
        return mpaStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("MPA с id=" + id + " не найден"));
    }
}
