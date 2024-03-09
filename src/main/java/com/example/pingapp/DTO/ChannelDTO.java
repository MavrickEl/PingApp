package com.example.pingapp.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChannelDTO {
    private Long id;
    private String token;
    private String chatId;
}
