package com.conference.roomservice.validation.annotation;

import com.conference.roomservice.validation.DurationMultipleOf15Validator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE}) //TODO leanr this
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DurationMultipleOf15Validator.class)

public @interface DurationMultipleOf15 {
    String message() default "The duration should be a multiple of 15";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
