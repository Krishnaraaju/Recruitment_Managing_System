package com.campus.recruitment.controller;

import com.campus.recruitment.dto.response.ApiResponse;
import com.campus.recruitment.dto.response.SkillResponse;
import com.campus.recruitment.service.SkillService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SkillResponse>>> getAllSkills() {
        return ResponseEntity.ok(ApiResponse.success("Skills retrieved successfully", skillService.getAllSkills()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SkillResponse>> createSkill(@RequestParam String name) {
        return new ResponseEntity<>(ApiResponse.success("Skill created successfully", skillService.createSkill(name)), HttpStatus.CREATED);
    }
}
