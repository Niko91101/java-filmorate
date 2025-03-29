package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
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
        return userStorage.add(user);
    }

    public User update(User user) {
        User existingUser = userStorage.getById(user.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + user.getId() + " не найден"));

        user.setFriends(existingUser.getFriends());

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
        log.info("Добавление в друзья: пользователь {} → {}", userId, friendId);

        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить самого себя в друзья.");
        }

        User user = getById(userId);
        User friend = getById(friendId);

        if (user.getFriends() == null || friend.getFriends() == null) {
            throw new ValidationException("Список друзей не инициализирован.");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        userStorage.update(user);
        userStorage.update(friend);

        log.info("Пользователи {} и {} теперь друзья", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        log.info("Удаление из друзей: пользователь {} → {}", userId, friendId);

        User user = getById(userId);
        User friend = getById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        userStorage.update(user);
        userStorage.update(friend);

        log.info("Пользователи {} и {} больше не друзья", userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        User user = getById(userId);
        if (user.getFriends() == null) {
            throw new ValidationException("Список друзей пуст или не инициализирован.");
        }
        return user.getFriends().stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        Set<Long> userFriends = getById(userId).getFriends();
        Set<Long> otherFriends = getById(otherId).getFriends();

        if (userFriends == null || otherFriends == null) {
            throw new ValidationException("Один из пользователей не имеет друзей.");
        }

        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(this::getById)
                .collect(Collectors.toList());
    }
}
