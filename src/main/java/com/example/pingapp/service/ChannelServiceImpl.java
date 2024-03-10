package com.example.pingapp.service;

import com.example.pingapp.DTO.ChannelDTO;
import com.example.pingapp.entity.Channel;
import com.example.pingapp.exception.ChannelException;
import com.example.pingapp.repo.ChannelRepo;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ChannelServiceImpl implements ChannelService {
    private final ChannelRepo channelRepo;
    private final ModelMapper mapper;

    public ChannelServiceImpl(ChannelRepo channelRepo) {
        this.channelRepo = channelRepo;
        this.mapper = new ModelMapper();
    }

    @Override
    public ChannelDTO save(ChannelDTO channelDTO) {
        Channel newChannel = mapper.map(channelDTO, Channel.class);
        log.info("Сохранение нового ТГ канала");
        return mapper.map(channelRepo.save(newChannel), ChannelDTO.class);
    }

    @Override
    public ChannelDTO getById(Long id) {
        log.info("Обработка запроса получения канала по идентификатору {}", id);
        return mapper.map(get(id), ChannelDTO.class);
    }

    @Override
    public List<ChannelDTO> getAll() {
        log.info("Обработка запроса получения всех каналов");
        return channelRepo.findAll().stream().map(channel -> mapper.map(channel, ChannelDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        get(id);
        log.info("Удаление канала по идентификатору {}", id);
        channelRepo.deleteById(id);
    }

    private Channel get(Long id) {
        return channelRepo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Канал с идентификатором {} не найден", id);
                    throw new ChannelException("Канал с идентификатором " +
                            id + " не найден");
                });
    }
}
