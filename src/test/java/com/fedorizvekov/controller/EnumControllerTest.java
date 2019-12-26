package com.fedorizvekov.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@TestMethodOrder(OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
public class EnumControllerTest {

    @InjectMocks
    private EnumController enumController;

    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(enumController).build();
    }


    @DisplayName("Should invoke addEnum and return OK")
    @Order(1)
    @Test
    void should_invoke_addEnum_and_return_OK() throws Exception {
        mockMvc.perform(put("/enums/{newEnum}", "test_enum"))
                .andExpect(status().isOk());
    }


    @DisplayName("Should return OK and DynamicEnum values as string")
    @Order(2)
    @Test
    void should_return_OK_and_DynamicEnum_values_as_string() throws Exception {
        mockMvc.perform(get("/enums"))
                .andExpect(status().isOk())
                .andExpect(content().string("[TEST_ENUM]"));
    }

}
