package com.campus.recruitment.service.impl;

import com.campus.recruitment.dto.request.CompanyRequest;
import com.campus.recruitment.dto.response.CompanyResponse;
import com.campus.recruitment.entity.Company;
import com.campus.recruitment.exception.ResourceNotFoundException;
import com.campus.recruitment.repository.CompanyRepository;
import com.campus.recruitment.service.CompanyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    @Transactional
    public CompanyResponse createCompany(CompanyRequest request) {
        Company company = new Company();
        company.setName(request.getName());
        company.setWebsite(request.getWebsite());
        company.setIndustry(request.getIndustry());
        company.setLogoUrl(request.getLogoUrl());

        return mapToResponse(companyRepository.save(company));
    }

    @Override
    @Transactional
    public CompanyResponse updateCompany(Long id, CompanyRequest request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
                
        company.setName(request.getName());
        company.setWebsite(request.getWebsite());
        company.setIndustry(request.getIndustry());
        company.setLogoUrl(request.getLogoUrl());

        return mapToResponse(companyRepository.save(company));
    }

    @Override
    public List<CompanyResponse> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CompanyResponse getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        return mapToResponse(company);
    }
    
    private CompanyResponse mapToResponse(Company company) {
        CompanyResponse res = new CompanyResponse();
        res.setId(company.getId());
        res.setName(company.getName());
        res.setWebsite(company.getWebsite());
        res.setIndustry(company.getIndustry());
        res.setLogoUrl(company.getLogoUrl());
        return res;
    }
}
