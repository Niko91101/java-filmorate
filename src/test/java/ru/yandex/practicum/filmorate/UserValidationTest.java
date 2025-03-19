package ru.yandex.practicum.filmorate;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidationTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldNotAllowEmptyEmail() {
        User user = new User();
        user.setEmail("");
        Set violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Email не должен быть пустым");
    }

    @Test
    void shouldNotAllowInvalidEmail() {
        User user = new User();
        user.setEmail("invalid-email");
        Set violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Email должен содержать @");
    }

    @Test
    void shouldNotAllowEmptyLogin() {
        User user = new User();
        user.setLogin("");
        Set violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Логин не должен быть пустым");
    }

    @Test
    void shouldNotAllowLoginWithSpaces() {
        User user = new User();
        user.setLogin("my login");
        Set violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Логин не должен содержать пробелы");
    }

    @Test
    void shouldNotAllowFutureBirthday() {
        User user = new User();
        user.setBirthday(LocalDate.now().plusDays(1));
        Set violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Дата рождения не может быть в будущем");
    }
}
