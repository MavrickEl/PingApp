package com.example.pingapp.controller;

import com.example.pingapp.DTO.ChannelDTO;
import com.example.pingapp.service.ChannelService;
import com.example.pingapp.service.PingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Channel", description = "channel controller")
@RestController
@Log4j2
@RequestMapping("/api/channel")
public class ChannelController {
    private final ChannelService channelService;
    private final PingService pingService;

    public ChannelController(ChannelService channelService, PingService pingService) {
        this.channelService = channelService;
        this.pingService = pingService;
    }

    @Operation(summary = "Сохранение нового канала (ADMIN)",
            description = "Сохранить новый канал может только администратор")
    @PostMapping()
    public ResponseEntity<Void> saveChannel(@RequestBody ChannelDTO channelDTO) {
        log.info("Запрос сохранения канала {}, адрес /api/channel", channelDTO);
        pingService.addChannel(channelService.save(channelDTO));
        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "Получение списка всех каналов из базы (ADMIN, USER)")
    @GetMapping()
    public ResponseEntity<List<ChannelDTO>> getAll() {
        log.info("Запрос получения списка каналов, адрес /api/channel");
        return ResponseEntity.ok().body(channelService.getAll());
    }

    @Operation(summary = "Получение канала из базы (ADMIN, USER)")
    @GetMapping("/{id}")
    public ResponseEntity<ChannelDTO> getById(@PathVariable Long id) {
        log.info("Запрос получения канала по id {}, адрес /api/channel/{}", id, id);
        return ResponseEntity.ok().body(channelService.getById(id));
    }

    @Operation(summary = "Удаление канала (ADMIN)",
            description = "Удалить канал может только администратор")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Запрос удаления канала с идентификатором {}, адрес /api/channel/{}", id, id);
        channelService.delete(id);
        pingService.deleteChannel(id);
        return ResponseEntity.ok().build();
    }
}
