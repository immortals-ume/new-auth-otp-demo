package com.demo.auth.authdemoproject.model.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

/**
 * Custom Error Object for handling Api Error
 * following properties are provided
 * <ol>
 *    <li>Http Status Code</li
 *    <li> timestamp for error occurred</li>
 *    <li>error message</li>
 *    <li>error description /stacktrace</li>
 * </ol>
 */
@Getter
@Setter
@AllArgsConstructor
public class ErrorDto {
    private HttpStatus status;

    private Instant errorTimeStamp;

    private String errorMessage;

    private List<String> errors;

    public ErrorDto(HttpStatus status, Instant errorTimeStamp, String errorMessage) {
        this.status = status;
        this.errorTimeStamp = errorTimeStamp;
        this.errorMessage = errorMessage;
    }
}
