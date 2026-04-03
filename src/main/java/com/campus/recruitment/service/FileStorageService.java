package com.campus.recruitment.service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

public interface FileStorageService {
    String storeFile(MultipartFile file, Long studentId);
    Resource loadFileAsResource(String fileName);
    void deleteFile(String fileName);
}
