package com.example.application.data.entity;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class SamplePerson extends AbstractEntity {

    private String firstName;
    private String lastName;
    @Email
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String occupation;
    private String role;
    private boolean important;

}
