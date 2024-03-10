package com.example.pingapp.utils;

import lombok.NoArgsConstructor;
/**
 * Утилита для создания URL-адресов для отправки сообщений в ТГ.
 * Этот класс предоставляет метод для создания URL-адреса на основе предоставленного токена бота Telegram,
 * идентификатора канала и текста сообщения
 */
@NoArgsConstructor
public class TGUrl {

    /**
     * Создает URL-адрес для отправки сообщения в ТГ.
     *
     * @param token Токен Telegram-бота.
     * @param channelId Идентификатор канала, на который будет отправлено сообщение.
     * @param message Текст отправляемого сообщения.
     * @return Созданный URL-адрес для отправки сообщения.
     */
    public static String getUrl(String token, String channelId, String message) {
        return "https://api.telegram.org/bot" +
                token +
                "/sendMessage?chat_id=-" +
                channelId +
                "&text=" +
                message;
    }
}
