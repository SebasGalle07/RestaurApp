package com.restaurapp.demo.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenPair {
    private String access_token;
    private Integer expires_in;
    private String refresh_token;
}
