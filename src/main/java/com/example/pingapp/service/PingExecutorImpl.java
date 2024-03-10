package com.example.pingapp.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PingExecutorImpl implements PingExecutor {
    private final RestTemplate template;

    public PingExecutorImpl() {
        this.template = new RestTemplate();
    }

    @Override
    public int ping(String url) {
        return template.getForEntity(url, String.class).getStatusCode().value();
    }
}
