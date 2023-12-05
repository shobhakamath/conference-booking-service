package com.conference.roomservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class BookingExceptionHandler {
    public static final String CLIENT_ERROR = "client_side_error";

    @ExceptionHandler(OperationNotAllowedException.class)
    @ResponseStatus(BAD_REQUEST)
    ErrorDTO operationNotAllowedExceptionHandler(final OperationNotAllowedException exception) {
        log.error(exception.getMessage(), exception);
        return ErrorDTO.builder()
                .code(CLIENT_ERROR)
                .messages(List.of(exception.getMessage()))
                .build();
    }

    @ExceptionHandler(OverlappingMaintenanceException.class)
    @ResponseStatus(BAD_REQUEST)
    ErrorDTO overlappingMaintenanceExceptionHandler(final OverlappingMaintenanceException exception) {
        log.error(exception.getMessage(), exception);
        return ErrorDTO.builder()
                .code(CLIENT_ERROR)
                .messages(List.of(exception.getMessage()))
                .build();
    }

    @ExceptionHandler(BookingNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    ErrorDTO bookingNotFoundExceptionHandler(final BookingNotFoundException exception) {
        log.error(exception.getMessage(), exception);
        return ErrorDTO.builder()
                .code(CLIENT_ERROR)
                .messages(List.of(exception.getMessage()))
                .build();
    }

    @ExceptionHandler(RoomNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    ErrorDTO roomNotFoundExceptionHandler(final RoomNotFoundException exception) {
        log.error(exception.getMessage(), exception);
        return ErrorDTO.builder()
                .code(CLIENT_ERROR)
                .messages(List.of(exception.getMessage()))
                .build();
    }

    @ExceptionHandler(java.time.format.DateTimeParseException.class)
    @ResponseStatus(BAD_REQUEST)
    ErrorDTO dateTimeParseExceptionErrorHandler(final DateTimeParseException exception) {
        return ErrorDTO.builder()
                .code(CLIENT_ERROR)
                .messages(List.of(exception.getMessage()))
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    ErrorDTO bodyValidatorConstraintErrorHandler(final MethodArgumentNotValidException exception) {
        final var errorMessages = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> String.format("%s: %s",
                        fieldError.getField(),
                        fieldError.getDefaultMessage()))
                .toList();

        log.error("{}", errorMessages, exception);
        return ErrorDTO.builder()
                .code(CLIENT_ERROR)
                .messages(errorMessages)
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    ErrorDTO handleOtherExceptions(final Exception exception) {
        return ErrorDTO.builder()
                .code(CLIENT_ERROR)
                .messages(List.of(exception.getMessage()))
                .build();
    }
}
