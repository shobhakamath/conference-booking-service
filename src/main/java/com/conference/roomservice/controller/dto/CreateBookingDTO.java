package com.conference.roomservice.controller.dto;

import com.conference.roomservice.validation.annotation.DurationMultipleOf15;
import com.conference.roomservice.validation.annotation.ShouldBeToday;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import static com.conference.roomservice.constant.ConferenceConstants.DATE_FORMAT_REGEX;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@DurationMultipleOf15
public class CreateBookingDTO implements Serializable {

    @Pattern(regexp = DATE_FORMAT_REGEX, message = "Not a valid date")
    @ShouldBeToday
    String date;


    @NotNull //TODO not working
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotNull
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @NotNull
    @Range(min = 1, max = 20, message = "should be greater than 1 and less than or equal to the maximum capacity")
    //@Builder.Default
    Integer noOfPersons = 0;

    @NotNull
    @NotEmpty
    String meetingTitle;

    @NotNull
    @NotEmpty
    String emailId;


    @JsonIgnore
    LocalDate conferenceDate;

    @JsonIgnore
    String uuid;


}

