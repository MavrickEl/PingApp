package com.example.pingapp.service;

import com.example.pingapp.DTO.RuleDTO;

import java.util.List;

public interface RuleService {
    RuleDTO save(RuleDTO ruleDTO);

    RuleDTO getById(Long id);

    List<RuleDTO> getAll();

    RuleDTO edit(Long id, boolean isActive);


    void delete(Long id);
}
