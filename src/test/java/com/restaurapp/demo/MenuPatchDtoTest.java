package com.restaurapp.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurapp.demo.dto.MenuPatchDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class MenuPatchDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void parsesIntegerPrice() throws Exception {
        String json = """
                {
                  "precio": 19000,
                  "activo": true
                }
                """;
        MenuPatchDto dto = mapper.readValue(json, MenuPatchDto.class);
        assertThat(dto.precio()).isEqualByComparingTo(BigDecimal.valueOf(19000));
        assertThat(dto.activo()).isTrue();
    }
}
