package com.example.pingapp.service;

public interface NotificationService {
    void sendMessage(String token, String chatId, String message);

    String generateNotificationMessage(int actualStatus, int expectedStatus, String url);
}
