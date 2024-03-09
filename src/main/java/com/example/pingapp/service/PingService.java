package com.example.pingapp.service;

import com.example.pingapp.DTO.RuleDTO;
import com.example.pingapp.entity.Channel;
import com.example.pingapp.entity.Rule;
import com.example.pingapp.utils.TGUrl;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Data
public class PingService {
    private final ModelMapper mapper = new ModelMapper();
    private Map<Long, ScheduledExecutorService> schedulerMap = new ConcurrentHashMap<>();
    private List<Rule> rules = new LinkedList<>();
    private List<Channel> channels = new LinkedList<>();
    private final RestTemplate template = new RestTemplate();
    private final RuleServiceImpl ruleService;

    public PingService(RuleServiceImpl ruleService) {
        this.ruleService = ruleService;
    }


    @Bean
    public CommandLineRunner pingTask() {
        rules = ruleService.loadRules();
        return args -> {
            for (Rule rule : rules) {
                createTreadForRule(rule);
            }
        };
    }

    private void createTreadForRule(Rule rule) {
        String url = rule.getUrl();
        long interval = rule.getIntervalSecond();
        int expectedStatusCode = rule.getExpectedStatusCode();
        Runnable task = () -> {
            String status = "";
            try {
                ResponseEntity<String> response = template.getForEntity(url, String.class);
                status = String.valueOf(response.getStatusCode());
                if (response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(expectedStatusCode))) {
//                    for (Channel channel : channels){
                    System.out.println(url + " successful: " + response.getStatusCode());
//                    }
                } else {
                    for (Channel channel : channels) {
                        template.getForEntity(TGUrl.getUrl(channel.getToken(),
                                channel.getChatId(),
                                "Получен статус " + response.getStatusCode() + ", при ожидаемом статусе " + expectedStatusCode + ", url " + url), String.class);
                    }
                }
            } catch (Exception httpClientOrServerExc) {
                System.out.println("PingService error: " + status);
            }
        };
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        schedulerMap.put(rule.getId(), scheduler);
        scheduler.scheduleAtFixedRate(task, 0, interval, TimeUnit.SECONDS);
    }

    public void removeThreadForRule(long ruleId) {
        ScheduledExecutorService scheduler = schedulerMap.get(ruleId);
        if (scheduler != null) {
            scheduler.shutdownNow();
            schedulerMap.remove(ruleId);
        } else {
            System.out.println("Thread not found for rule with ID: " + ruleId);
        }
    }

    public void addThreadForRule(RuleDTO ruleDTO) {
        createTreadForRule(mapper.map(ruleDTO, Rule.class));
    }
}
