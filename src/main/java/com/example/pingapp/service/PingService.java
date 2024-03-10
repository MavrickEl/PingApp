package com.example.pingapp.service;

import com.example.pingapp.DTO.ChannelDTO;
import com.example.pingapp.DTO.RuleDTO;

public interface PingService {
    void removeThreadForRule(long ruleId);

    void addThreadForRule(RuleDTO ruleDTO);

    void addChannel(ChannelDTO channelDTO);

}
