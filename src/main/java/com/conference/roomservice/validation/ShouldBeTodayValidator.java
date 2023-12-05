package com.conference.roomservice.validation;

import com.conference.roomservice.validation.annotation.ShouldBeToday;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static com.conference.roomservice.constant.ConferenceConstants.DATE_FORMATTER;

public class ShouldBeTodayValidator implements ConstraintValidator<ShouldBeToday, String> {

    @Override
    public void initialize(ShouldBeToday constraintAnnotation) {
    }

    @Override
    public boolean isValid(String date, ConstraintValidatorContext context) {
        // Parse the string to LocalDate
        try {
            LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);
            return localDate.isEqual(LocalDate.now());
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date: " + e.getMessage());
        }
        return false;
    }
}
