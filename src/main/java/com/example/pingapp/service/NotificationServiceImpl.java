package com.example.pingapp.service;

import com.example.pingapp.utils.TGUrl;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Реализация интерфейса {@link NotificationService}, который
 * создает и отправляет уведомления в ТГ, основываясь на информации о состоянии.
 */
@Service
public class NotificationServiceImpl implements NotificationService {
    private final RestTemplate template;

    public NotificationServiceImpl() {
        this.template = new RestTemplate();
    }

    /**
     * Отправляет уведомление в указанный чат.
     *
     * @param token   Токен Telegram-бота.
     * @param chatId  Идентификатор чата, куда будет отправлено сообщение.
     * @param message Текст отправляемого сообщения.
     */
    @Override
    public void sendMessage(String token, String chatId, String message) {
        String apiUrl = TGUrl.getUrl(token, chatId, message);
        try {
            template.getForEntity(apiUrl, String.class);
        } catch (Exception e) {
            System.out.println("Не удалось отправить уведомление в Telegram.: " + e.getMessage());
        }
    }

    /**
     * Генерирует уведомление на основе фактического и ожидаемого статуса,
     * и URL-адреса, связанного с проверкой статуса.
     *
     * @param actualStatus   Фактический полученный статус.
     * @param expectedStatus Ожидаемый статус.
     * @param url            URL-адрес, связанный с проверкой статуса.
     * @return Сгенерированное уведомление.
     */
    @Override
    public String generateNotificationMessage(int actualStatus, int expectedStatus, String url) {
        String notificationMessage;
        if (actualStatus == expectedStatus) {
            notificationMessage = "Ожидаемый статус "
                    + expectedStatus + " снова получен после статуса "
                    + actualStatus + ", url "
                    + url;
        } else {
            notificationMessage = "Получен статус "
                    + actualStatus + ", при ожидаемом статусе "
                    + expectedStatus + ", url "
                    + url;
        }
        return notificationMessage;
    }
}
