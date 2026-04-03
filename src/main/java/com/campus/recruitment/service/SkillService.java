package com.campus.recruitment.service;

import com.campus.recruitment.dto.response.SkillResponse;
import java.util.List;

public interface SkillService {
    SkillResponse createSkill(String name);
    List<SkillResponse> getAllSkills();
}
