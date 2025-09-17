package com.restaurapp.demo.dto;

import com.restaurapp.demo.dto.TokenPair;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private boolean success;
    private TokenPair data;
    private String message;
}
