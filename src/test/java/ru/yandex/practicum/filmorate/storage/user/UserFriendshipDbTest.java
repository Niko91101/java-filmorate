package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, UserMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserFriendshipDbTest {

    private final UserDbStorage userDbStorage;

    @Test
    void shouldAddAndRemoveFriend() {
        User user1 = createUser("one@example.com", "user1");
        User user2 = createUser("two@example.com", "user2");

        userDbStorage.addFriend(user1.getId(), user2.getId());

        List<User> friends = userDbStorage.getFriends(user1.getId());

        assertThat(friends)
                .hasSize(1)
                .extracting(User::getId)
                .contains(user2.getId());

        userDbStorage.removeFriend(user1.getId(), user2.getId());

        List<User> updatedFriends = userDbStorage.getFriends(user1.getId());

        assertThat(updatedFriends).isEmpty();
    }

    @Test
    void shouldReturnCommonFriends() {
        User user1 = createUser("one@example.com", "user1");
        User user2 = createUser("two@example.com", "user2");
        User commonFriend = createUser("common@example.com", "common");

        userDbStorage.addFriend(user1.getId(), commonFriend.getId());
        userDbStorage.addFriend(user2.getId(), commonFriend.getId());

        List<User> commonFriends = userDbStorage.getCommonFriends(user1.getId(), user2.getId());

        assertThat(commonFriends)
                .hasSize(1)
                .extracting(User::getId)
                .contains(commonFriend.getId());
    }

    private User createUser(String email, String login) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName("Test " + login);
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return userDbStorage.add(user);
    }
}
