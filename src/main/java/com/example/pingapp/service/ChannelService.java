package com.example.pingapp.service;

import com.example.pingapp.DTO.ChannelDTO;

import java.util.List;

public interface ChannelService {
    ChannelDTO save(ChannelDTO channelDTO);

    ChannelDTO getById(Long id);

    List<ChannelDTO> getAll();

    void delete(Long id);
}
