package com.example.pingapp.utils;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TGUrl {
    public static String getUrl(String token, String channelId, String message) {
        return "https://api.telegram.org/bot" +
                token +
                "/sendMessage?chat_id=-" +
                channelId +
                "&text=" +
                message;
    }
}
