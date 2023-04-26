package com.example.application.data.entity;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
public class Contract extends AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String contractNumber;
    private String contractType;
    private LocalDate signingDate;
    private String contractSigningPlace;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private String contractSubject;
    //TODO skonfigurowac na bigDecimal
    private Double totalValue;
    private String contractorNIP;
    private String contractorName;
    private String contractorAddress;
    private String sendToMF;
}
