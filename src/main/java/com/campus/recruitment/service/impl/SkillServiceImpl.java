package com.campus.recruitment.service.impl;

import com.campus.recruitment.dto.response.SkillResponse;
import com.campus.recruitment.entity.Skill;
import com.campus.recruitment.exception.BadRequestException;
import com.campus.recruitment.repository.SkillRepository;
import com.campus.recruitment.service.SkillService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    public SkillServiceImpl(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Override
    public SkillResponse createSkill(String name) {
        if (skillRepository.findByName(name).isPresent()) {
            throw new BadRequestException("Skill already exists");
        }
        
        Skill skill = new Skill();
        skill.setName(name);
        skill = skillRepository.save(skill);
        
        return new SkillResponse(skill.getId(), skill.getName());
    }

    @Override
    public List<SkillResponse> getAllSkills() {
        return skillRepository.findAll().stream()
                .map(s -> new SkillResponse(s.getId(), s.getName()))
                .collect(Collectors.toList());
    }
}
