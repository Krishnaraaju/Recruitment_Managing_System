package com.campus.recruitment.service;

import com.campus.recruitment.dto.request.CategoryRequest;
import com.campus.recruitment.dto.response.CategoryResponse;
import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(Long id, CategoryRequest request);
    void deleteCategory(Long id);
    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategoryById(Long id);
}
