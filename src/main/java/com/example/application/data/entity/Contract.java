package com.example.application.data.entity;

import jakarta.persistence.Entity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
public class Contract extends AbstractEntity{

    private String contractId;
    private String integrationStatus;
    private String contractNumber;
    private LocalDate registrationDate;
    private String status;
    private LocalDate signingDate;
    private String contractSubject;
    //TODO poprawic na bigDecimal
    private Double totalValue;
    private String contractorNIP;
    private String contractorName;
    private String contractorNumber;
    private String userRegistryId;
}
