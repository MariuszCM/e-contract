package com.example.application.data.service;

import com.example.application.data.entity.Contract;
import com.example.application.data.entity.SamplePerson;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContractService {
    private final ContractRepository contractRepository;

    public Contract save(Contract entity) {
        return contractRepository.save(entity);
    }

    public Page<Contract> list(Pageable pageable, Specification<Contract> filter) {
        return contractRepository.findAll(filter, pageable);
    }
}
