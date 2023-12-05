package com.conference.roomservice.validation.annotation;

import com.conference.roomservice.validation.ShouldBeTodayValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ShouldBeTodayValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ShouldBeToday {

    String message() default "Date must be today";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

