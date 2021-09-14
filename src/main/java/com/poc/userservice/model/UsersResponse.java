package com.poc.userservice.model;

import lombok.Data;

@Data
public class UsersResponse {

    private Object users;

    private ErrorDetails errorDetails;

}
