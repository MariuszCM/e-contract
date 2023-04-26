package com.example.application.regonApi.model;

import lombok.Data;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@Data
@XmlRootElement(name = "root")
public class SearchResponseWrapper {
    private List<CompanyIntegration> data;


    @XmlElement(name = "dane")
    public void setData(List<CompanyIntegration> data) {
        this.data = data;
    }
}
