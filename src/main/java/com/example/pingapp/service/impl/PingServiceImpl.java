package com.example.pingapp.service.impl;

import com.example.pingapp.DTO.ChannelDTO;
import com.example.pingapp.DTO.RuleDTO;
import com.example.pingapp.service.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Реализация интерфейса {@link PingService}, отвечающего за управление и выполнение задач ping на основе определенных правил.
 * Использует ScheduledExecutorService для периодического выполнения задач ping и уведомляет каналы при изменении статуса.
 */
@Component
public class PingServiceImpl implements PingService {
    private final Map<Long, ScheduledExecutorService> schedulerMap = new ConcurrentHashMap<>();
    private final Map<Long, Long> lastStatusMap = new ConcurrentHashMap<>();
    private final List<RuleDTO> rules = new LinkedList<>();
    private final List<ChannelDTO> channels = new LinkedList<>();
    private final RuleService ruleService;
    private final ChannelService channelService;
    private final PingExecutor pingExecutor;
    private final NotificationService notificationService;

    public PingServiceImpl(RuleService ruleService, ChannelService channelService, PingExecutor pingExecutor, NotificationService notificationService) {
        this.ruleService = ruleService;
        this.channelService = channelService;
        this.pingExecutor = pingExecutor;
        this.notificationService = notificationService;
    }


    @PostConstruct
    public void pingTask() {
        initCollections();
        for (RuleDTO rule : rules) {
            createTreadForRule(rule);
        }
    }

    private void initCollections() {
        rules.addAll(ruleService.getActiveRules());
        for (RuleDTO rule : rules) {
            lastStatusMap.put(rule.getId(), (long) rule.getExpectedStatusCode());
        }
        channels.addAll(channelService.getAll());
    }

    private void createTreadForRule(RuleDTO rule) {
        String url = rule.getUrl();
        int expectedStatusCode = rule.getExpectedStatusCode();
        Runnable task = () -> {
            int status;
            try {
                status = pingExecutor.ping(url);
                notifyChannels(rule.getId(), status, expectedStatusCode, url);
            } catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc) {
                int statusException = httpClientOrServerExc.getStatusCode().value();
                notifyChannels(rule.getId(), statusException, expectedStatusCode, url);
            } catch (Exception e) {
                System.out.println("PingServiceImpl error: " + e.getMessage());
            }
        };
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        schedulerMap.put(rule.getId(), scheduler);
        scheduler.scheduleAtFixedRate(task, 0, rule.getIntervalSecond(), TimeUnit.SECONDS);
    }

    private void notifyChannels(long ruleId, int actualStatus, int expectedStatus, String url) {
        if (lastStatusMap.containsKey(ruleId) && lastStatusMap.get(ruleId) == actualStatus) {
            return;
        }
        lastStatusMap.put(ruleId, (long) actualStatus);
        String message = notificationService.generateNotificationMessage(actualStatus, expectedStatus, url);
        for (ChannelDTO channel : channels) {
            notificationService.sendMessage(channel.getToken(), channel.getChatId(), message);
        }
    }

    /**
     * Удаляет поток, который посылает запросы на сервер по определенному правилу.
     *
     * @param ruleId Идентификатор правила, для которого необходимо удалить поток.
     */
    public void removeThreadForRule(long ruleId) {
        ScheduledExecutorService scheduler = schedulerMap.get(ruleId);
        if (scheduler != null) {
            scheduler.shutdownNow();
            schedulerMap.remove(ruleId);
        } else {
            System.out.println("Thread not found for rule with ID: " + ruleId);
        }
    }

    /**
     * Добавляет новый поток для указанного правила.
     *
     * @param ruleDTO DTO, содержащий информацию о правиле.
     */
    public void addThreadForRule(RuleDTO ruleDTO) {
        lastStatusMap.put(ruleDTO.getId(), (long) ruleDTO.getExpectedStatusCode());
        createTreadForRule(ruleDTO);
    }

    public void addChannel(ChannelDTO channelDTO) {
        channels.add(channelDTO);
    }
}
