package com.example.pingapp.service.impl;

import com.example.pingapp.DTO.RuleDTO;
import com.example.pingapp.entity.Rule;
import com.example.pingapp.exception.RuleException;
import com.example.pingapp.repo.RuleRepo;
import com.example.pingapp.service.RuleService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class RuleServiceImpl implements RuleService {

    private final RuleRepo ruleRepo;
    private final ModelMapper mapper;

    public RuleServiceImpl(RuleRepo ruleRepo) {
        this.ruleRepo = ruleRepo;
        this.mapper = new ModelMapper();
    }


    @Override
    public RuleDTO save(RuleDTO ruleDTO) {
        Rule newRule = mapper.map(ruleDTO, Rule.class);
        log.info("Сохранение нового правила");
        if (ruleDTO.getIntervalSecond() <= 0) {
            throw new RuleException("Интервал правила должен быть больше 0");
        }
        newRule = ruleRepo.save(newRule);
        return mapper.map(newRule, RuleDTO.class);
    }

    @Override
    public RuleDTO getById(Long id) {
        log.info("Обработка запроса получения правила по идентификатору {}", id);
        return mapper.map(get(id), RuleDTO.class);
    }

    @Override
    public List<RuleDTO> getAll() {
        log.info("Обработка запроса получения всех правил");
        return ruleRepo.findAll().stream().map(rule -> mapper.map(rule, RuleDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public RuleDTO edit(Long id, boolean isActive) {
        Rule rule = get(id);
        log.info("Редактирование правила по идентификатору {}", id);
        rule.setActive(isActive);
        return mapper.map(ruleRepo.save(rule), RuleDTO.class);
    }

    @Override
    public List<RuleDTO> getActiveRules() {
        return ruleRepo.findRulesByActiveIsTrue().stream().map(rule -> mapper.map(rule, RuleDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        get(id);
        log.info("Удаление правила по идентификатору {}", id);
        ruleRepo.deleteById(id);
    }

    private Rule get(Long id) {
        return ruleRepo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Правило с идентификатором {} не найдено", id);
                    throw new RuleException("Правило с идентификатором " +
                            id + " не найдено");
                });
    }
}
