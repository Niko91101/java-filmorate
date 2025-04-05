package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, UserMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final UserDbStorage userDbStorage;

    @Test
    void shouldAddAndFindUserById() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User addedUser = userDbStorage.add(user);
        Optional<User> userOptional = userDbStorage.getById(addedUser.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u)
                                .hasFieldOrPropertyWithValue("id", addedUser.getId())
                                .hasFieldOrPropertyWithValue("email", "test@example.com")
                );
    }

    @Test
    void shouldUpdateUser() {
        User user = new User();
        user.setEmail("original@example.com");
        user.setLogin("originaluser");
        user.setName("Original");
        user.setBirthday(LocalDate.of(1995, 5, 5));

        User addedUser = userDbStorage.add(user);

        addedUser.setEmail("updated@example.com");
        addedUser.setLogin("updateduser");
        addedUser.setName("Updated");
        addedUser.setBirthday(LocalDate.of(1996, 6, 6));

        userDbStorage.update(addedUser);
        Optional<User> updated = userDbStorage.getById(addedUser.getId());

        assertThat(updated)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u)
                                .hasFieldOrPropertyWithValue("email", "updated@example.com")
                                .hasFieldOrPropertyWithValue("login", "updateduser")
                                .hasFieldOrPropertyWithValue("name", "Updated")
                );
    }

    @Test
    void shouldReturnAllUsers() {
        User user1 = new User();
        user1.setEmail("one@example.com");
        user1.setLogin("one");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        userDbStorage.add(user1);

        User user2 = new User();
        user2.setEmail("two@example.com");
        user2.setLogin("two");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(1992, 2, 2));
        userDbStorage.add(user2);

        List<User> users = userDbStorage.findAll();

        assertThat(users)
                .isNotNull()
                .hasSizeGreaterThanOrEqualTo(2)
                .extracting(User::getEmail)
                .contains("one@example.com", "two@example.com");
    }
}
