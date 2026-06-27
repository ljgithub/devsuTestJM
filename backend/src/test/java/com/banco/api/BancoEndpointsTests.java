package com.banco.api;

import com.banco.api.dto.MovimientoDTO;
import com.banco.api.exception.CustomException;
import com.banco.api.service.MovimientoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BancoEndpointsTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovimientoService movimientoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateMovimiento_SaldoNoDisponible() throws Exception {
        MovimientoDTO request = new MovimientoDTO(null, null, "Retiro", new BigDecimal("-200.00"), null, "478758");

        Mockito.when(movimientoService.createMovimiento(Mockito.any(MovimientoDTO.class)))
                .thenThrow(new CustomException("Saldo no disponible"));

        mockMvc.perform(post("/movimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Saldo no disponible"));
    }

    @Test
    public void testCreateMovimiento_CupoExcedido() throws Exception {
        MovimientoDTO request = new MovimientoDTO(null, null, "Retiro", new BigDecimal("-1200.00"), null, "478758");

        Mockito.when(movimientoService.createMovimiento(Mockito.any(MovimientoDTO.class)))
                .thenThrow(new CustomException("Cupo diario Excedido"));

        mockMvc.perform(post("/movimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cupo diario Excedido"));
    }
}
