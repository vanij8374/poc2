package com.poc.userservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@Table(name = "cuser")
public class User {
    @Id
    @NotEmpty(message = "id should not be null or empty")
    private String id;
    @NotEmpty(message = "name should not be null or empty")
    private String name;
    @NotEmpty(message = "name should not be null or empty")
    private String surname;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate joiningDate;

    @NotNull(message = "pincode should not be null or empty")
    private Integer pinCode;
    @NotNull(message = "isDeleted should not be null ")
    private boolean isDeleted;
}
