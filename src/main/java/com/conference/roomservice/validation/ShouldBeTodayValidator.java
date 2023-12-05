package com.conference.roomservice.validation;

import com.conference.roomservice.validation.annotation.ShouldBeToday;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static com.conference.roomservice.constant.ConferenceConstants.DATE_FORMATTER;

public class ShouldBeTodayValidator implements ConstraintValidator<ShouldBeToday, String> {
    private static final Logger logger = LoggerFactory.getLogger(ShouldBeTodayValidator.class);


    @Override
    public boolean isValid(String date, ConstraintValidatorContext context) {
        // Parse the string to LocalDate
        if (date != null && !date.isBlank()) {
            try {
                LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);
                return localDate.isEqual(LocalDate.now());
            } catch (DateTimeParseException e) {
                logger.info(new StringBuilder().append("Invalid date: ").append(e.getMessage()).toString());
            }
        }
        return false;
    }
}
