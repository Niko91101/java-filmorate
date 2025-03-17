package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDate, LocalDate> {
    private LocalDate minDate;

    @Override
    public void initialize(ReleaseDate constraintAnnotation) {
        try {
            this.minDate = LocalDate.parse(constraintAnnotation.value());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Некорректный формат даты в аннотации @ReleaseDate", e);
        }
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        return date == null || !date.isBefore(minDate);
    }
}
