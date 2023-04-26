package com.example.application.regonApi.model;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class CompanyModelConverter {

    public CompanyIntegration convert(String companyXml, String nip) throws Exception {
        SearchResponseWrapper wrapped = unmarshalCompanyXml(companyXml);
        validateSearchResponse(wrapped);

        CompanyIntegration prompt = wrapped.getData().get(0);
        prompt.setNip(nip);
        return prompt;
    }

    public void convertAdditionalInfo(CompanyIntegration sourceCompany, String companyXml) throws Exception {
        SearchResponseWrapper wrapped = unmarshalCompanyXml(companyXml);
        validateSearchResponse(wrapped);

        CompanyIntegration prompt = wrapped.getData().get(0);
        sourceCompany.setEmail(prompt.getEmail());
    }

    private SearchResponseWrapper unmarshalCompanyXml(String companyXml) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(SearchResponseWrapper.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        InputStream stream = new ByteArrayInputStream(companyXml.getBytes(StandardCharsets.UTF_8));
        return (SearchResponseWrapper) jaxbUnmarshaller.unmarshal(stream);
    }

    private void validateSearchResponse(SearchResponseWrapper wrapped) throws Exception {
        if (wrapped.getData() == null || wrapped.getData().isEmpty()) {
            throw new Exception("Company not found");
        }

        if (wrapped.getData().size() != 1) {
            throw new Exception("found more than one company.");
        }
    }
}
