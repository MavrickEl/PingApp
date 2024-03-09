package com.example.pingapp.controller;

import com.example.pingapp.DTO.RuleDTO;
import com.example.pingapp.service.PingService;
import com.example.pingapp.service.RuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Rule", description = "rule controller")
@RestController
@Log4j2
@RequestMapping("/api/rule")
public class RuleController {
    private final RuleService ruleService;
    private final PingService pingService;

    public RuleController(RuleService ruleService, PingService pingService) {
        this.ruleService = ruleService;
        this.pingService = pingService;
    }

    @Operation(summary = "Сохранение нового правила (ADMIN)",
            description = "Сохранить новое правило может только администратор")
    @PostMapping()
    public ResponseEntity<Void> saveRule(@RequestBody RuleDTO ruleDTO) {
        log.info("Запрос сохранения правила {}, адрес /api/rule", ruleDTO);
        pingService.addThreadForRule(ruleService.save(ruleDTO));
        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "Получение списка всех правил из базы (ADMIN, USER)")
    @GetMapping()
    public ResponseEntity<List<RuleDTO>> getAll() {
        log.info("Запрос получения списка правил, адрес /api/rule");
        return ResponseEntity.ok().body(ruleService.getAll());
    }

    @Operation(summary = "Получение правила из базы (ADMIN, USER)")
    @GetMapping("/{id}")
    public ResponseEntity<RuleDTO> getById(@PathVariable Long id) {
        log.info("Запрос получения правила по id {}, адрес /api/rule/{}", id, id);
        return ResponseEntity.ok().body(ruleService.getById(id));
    }

    @Operation(summary = "Обновление правила (ADMIN)",
            description = "Обновление одного поля правила 'active', доступно только администратору")
    @PatchMapping("/{id}")
    public ResponseEntity<RuleDTO> edit(@PathVariable Long id, @RequestParam boolean isActive) {
        log.info("Запрос редактирования правила с идентификатором {} адрес /api/rule/{}", id, id);
        RuleDTO ruleDTO = ruleService.edit(id, isActive);
        if (isActive) {
            pingService.addThreadForRule(ruleDTO);
        } else {
            pingService.removeThreadForRule(id);
        }
        return ResponseEntity.ok().body(ruleDTO);
    }

    @Operation(summary = "Удаление правила (ADMIN)",
            description = "Удалить правило может только администратор")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Запрос удаления правила с идентификатором {}, адрес /api/rule/{}", id, id);
        ruleService.delete(id);
        pingService.removeThreadForRule(id);
        return ResponseEntity.ok().build();
    }
}
