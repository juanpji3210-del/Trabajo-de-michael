package com.aprendiz.demo.controller;

import com.aprendiz.demo.dto.ProductoRequest;
import com.aprendiz.demo.dto.ProductoResponse;
import com.aprendiz.demo.exception.ProductoNotFoundException;
import com.aprendiz.demo.service.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    private static final String URL = "/api/productos";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductoService service;

    private ProductoResponse respuesta() {
        return new ProductoResponse(1L, "Camiseta", "Algodón", new BigDecimal("35000"), 10);
    }

    private ProductoRequest request() {
        return new ProductoRequest("Camiseta", "Algodón", new BigDecimal("35000"), 10);
    }

    @Test
    void listar_retorna200ConLaLista() throws Exception {
        when(service.listar(null)).thenReturn(List.of(respuesta()));

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Camiseta"));
    }

    @Test
    void buscarPorId_existente_retorna200() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(respuesta());

        mockMvc.perform(get(URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void buscarPorId_inexistente_retorna404() throws Exception {
        when(service.buscarPorId(99L)).thenThrow(new ProductoNotFoundException(99L));

        mockMvc.perform(get(URL + "/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void crear_valido_retorna201ConLocation() throws Exception {
        when(service.crear(any(ProductoRequest.class))).thenReturn(respuesta());

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/productos/1"))
                .andExpect(jsonPath("$.nombre").value("Camiseta"));
    }

    @Test
    void crear_invalido_retorna400() throws Exception {
        ProductoRequest invalido = new ProductoRequest("", null, new BigDecimal("-5"), -1);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void actualizar_valido_retorna200() throws Exception {
        when(service.actualizar(eq(1L), any(ProductoRequest.class))).thenReturn(respuesta());

        mockMvc.perform(put(URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(10));
    }

    @Test
    void eliminar_existente_retorna204() throws Exception {
        mockMvc.perform(delete(URL + "/1"))
                .andExpect(status().isNoContent());

        verify(service).eliminar(1L);
    }

    @Test
    void eliminar_inexistente_retorna404() throws Exception {
        doThrow(new ProductoNotFoundException(99L)).when(service).eliminar(99L);

        mockMvc.perform(delete(URL + "/99"))
                .andExpect(status().isNotFound());
    }
}
