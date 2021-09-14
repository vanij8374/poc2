package com.poc.userservice.config;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.poc.userservice.model.ErrorDetails;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class UserExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity(error("Validation Failed",errors), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Throwable tx = ex.getRootCause();
        String error;
        Map<String, String> errors = new HashMap<>();
        ErrorDetails errorDetails = null;
        if(tx instanceof DateTimeParseException){
            String fieldName = ((InvalidFormatException)ex.getCause()).getPath().get(0).getFieldName();
            error = fieldName+" should not be null or it should be in this format (yyyy-MM-dd)";
            errors.put(fieldName,error);
            errorDetails = error("Validation Failed",errors);
        }
        return new ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<String> userNotFound() {
        return ResponseEntity.badRequest().body("user not found to delete");
    }

    private ErrorDetails error(String message,Map<String,String> errors){
        return new ErrorDetails(new Date(), message,
                errors);
    }
}
