package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private long nextId = 1;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@Valid @RequestBody User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(nextId++);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Пользователь с id {} не найден", user.getId());
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }
        users.put(user.getId(), user);
        log.info("Обновлен пользователь {} ", user);
        return user;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
