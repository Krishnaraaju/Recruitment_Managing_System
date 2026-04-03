package com.campus.recruitment.service;

import com.campus.recruitment.dto.request.CompanyRequest;
import com.campus.recruitment.dto.response.CompanyResponse;
import java.util.List;

public interface CompanyService {
    CompanyResponse createCompany(CompanyRequest request);
    CompanyResponse updateCompany(Long id, CompanyRequest request);
    List<CompanyResponse> getAllCompanies();
    CompanyResponse getCompanyById(Long id);
}
