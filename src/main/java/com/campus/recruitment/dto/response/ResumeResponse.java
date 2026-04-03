package com.campus.recruitment.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ResumeResponse {
    private Long id;
    private Long studentId;
    private String fileName;
    private boolean isPrimary;
    private LocalDateTime uploadedAt;
}
