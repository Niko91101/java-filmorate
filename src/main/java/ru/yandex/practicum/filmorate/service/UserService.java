package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User add(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }

        return userStorage.add(user);
    }

    public User update(User user) {
        User existingUser = getById(user.getId());

        if (user.getFriends() == null) {
            user.setFriends(existingUser.getFriends());
        }

        return userStorage.update(user);
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User getById(Long id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить самого себя в друзья.");
        }

        User user = getById(userId);
        User friend = getById(friendId);

        if (user.getFriends() == null) user.setFriends(new HashSet<>());
        if (friend.getFriends() == null) friend.setFriends(new HashSet<>());

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        userStorage.update(user);
        userStorage.update(friend);

        log.info("Добавлены друзья: {} ↔ {}", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getById(userId);
        User friend = getById(friendId);

        if (user.getFriends() != null) {
            user.getFriends().remove(friendId);
        }

        if (friend.getFriends() != null) {
            friend.getFriends().remove(userId);
        }

        userStorage.update(user);
        userStorage.update(friend);

        log.info("Удалены друзья: {} и {}", userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        User user = getById(userId);
        if (user.getFriends() == null) {
            return Collections.emptyList();
        }

        return user.getFriends().stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        Set<Long> userFriends = getById(userId).getFriends();
        Set<Long> otherFriends = getById(otherId).getFriends();

        if (userFriends == null || otherFriends == null) {
            return Collections.emptyList();
        }

        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(this::getById)
                .collect(Collectors.toList());
    }
}
