package com.example.pingapp.DTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RuleDTO {
    private Long id;
    private String url;
    private int intervalSecond;
    private int expectedStatusCode;
    private boolean active;
}
