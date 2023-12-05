package com.conference.roomservice.validation;

import com.conference.roomservice.controller.dto.CreateBookingDTO;
import com.conference.roomservice.validation.annotation.DurationMultipleOf15;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Duration;
import java.time.LocalDate;

import static com.conference.roomservice.constant.ConferenceConstants.DATE_FORMATTER;

public class DurationMultipleOf15Validator implements ConstraintValidator<DurationMultipleOf15, CreateBookingDTO> {

    @Override
    public boolean isValid(CreateBookingDTO bookingDTO, ConstraintValidatorContext context) {
        bookingDTO.setConferenceDate(LocalDate.parse(bookingDTO.getDate(), DATE_FORMATTER));
        long durationInMinutes= Duration.between(bookingDTO.getStartTime(),bookingDTO.getEndTime()).toMinutes();
        boolean isValid=true;
        if(durationInMinutes<0){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("The end time is less than the start time.")
                    .addPropertyNode("endTime")  // Specify the property that is in error
                    .addConstraintViolation();
            isValid=false;

        }
        if (durationInMinutes % 15 != 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("The duration must be a multiple of 15 minutes.")
                    .addPropertyNode("endTime")  // Specify the property that is in error
                    .addConstraintViolation();
            isValid=false;
        }
        return isValid;
    }
}
