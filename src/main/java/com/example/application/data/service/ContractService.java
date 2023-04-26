package com.example.application.data.service;

import com.example.application.data.entity.Contract;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContractService {
    private final ContractRepository contractRepository;

    public Contract save(Contract entity) {
        return contractRepository.save(entity);
    }
}
