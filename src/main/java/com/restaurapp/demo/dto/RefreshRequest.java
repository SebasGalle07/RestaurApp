package com.restaurapp.demo.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshRequest {
    private String refresh_token;
}
