package com.poc.userservice.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Getter
public class ErrorDetails {

    private Date date;

    private String status;

    private Map<String,String> errors;

    public ErrorDetails(Date date, String status, Map<String, String> errors) {
        this.date = date;
        this.status = status;
        this.errors = errors;
    }
}
