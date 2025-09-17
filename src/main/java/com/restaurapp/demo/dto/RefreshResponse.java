package com.restaurapp.demo.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshResponse {
    private boolean success;
    private TokenPair data;
    private String message;
}
