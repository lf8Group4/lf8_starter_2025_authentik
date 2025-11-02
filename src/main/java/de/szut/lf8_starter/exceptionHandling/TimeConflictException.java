package de.szut.lf8_starter.exceptionHandling;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT) // HTTP 409
public class TimeConflictException extends RuntimeException {
    public TimeConflictException(String message) {
        super(message);
    }
}