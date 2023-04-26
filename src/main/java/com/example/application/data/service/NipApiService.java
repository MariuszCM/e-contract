package com.example.application.data.service;

import com.example.application.regonApi.client.RegonApiWebClientActions;
import com.example.application.regonApi.client.ReportType;
import com.example.application.regonApi.model.CompanyIntegration;
import com.example.application.regonApi.model.CompanyModelConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NipApiService {
    private final RegonApiWebClientActions regenApiClient;
    private final CompanyModelConverter modelConverter;
    private final ReportType reportType = ReportType.PublDaneRaportPrawna;

    public CompanyIntegration getFullCompanyReport(String nip) {
        try {
            String sessionId = regenApiClient.login();
            CompanyIntegration companyByNip = findCompanyByNip(nip, sessionId);
            return getCompanyReport(companyByNip, sessionId);
        } catch (Exception e) { }
        return new CompanyIntegration();
    }

    private CompanyIntegration findCompanyByNip(String nip, String sessionId) throws Exception {
        String xmlPrompt = regenApiClient.search(nip, sessionId);
        return modelConverter.convert(xmlPrompt, nip);
    }

    private CompanyIntegration getCompanyReport(CompanyIntegration companyIntegration, String sessionId) throws Exception {
        String report = regenApiClient.getReport(companyIntegration.getRegon(), reportType, sessionId);
        modelConverter.convertAdditionalInfo(companyIntegration, report);

        regenApiClient.logout(sessionId);
        return companyIntegration;
    }
}
