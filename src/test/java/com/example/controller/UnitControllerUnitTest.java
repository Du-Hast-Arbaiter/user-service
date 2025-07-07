package com.example.controller;

import com.example.dto.UserRequest;
import com.example.dto.UserResponse;
import com.example.exception.ResourceNotFoundException;
import com.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateUser() throws Exception {
        UserRequest request = new UserRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setAge(30);

        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setName(request.getName());
        response.setEmail(request.getEmail());
        response.setAge(request.getAge());

        when(userService.createUser(any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.age").value(30));
    }

    @Test
    void shouldReturnBadRequestForInvalidInput() throws Exception {
        UserRequest request = new UserRequest();
        request.setName(""); // Invalid name
        request.setEmail("invalid-email");
        request.setAge(-5); // Invalid age

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.age").exists());
    }

    @Test
    void shouldGetUserById() throws Exception {
        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setName("John Doe");
        response.setEmail("john@example.com");
        response.setAge(30);

        when(userService.getUserById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.age").value(30));
    }

    @Test
    void shouldReturnNotFoundForInvalidId() throws Exception {
        when(userService.getUserById(anyLong())).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        UserResponse user = new UserResponse();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setAge(30);

        when(userService.getAllUsers()).thenReturn(Collections.singletonList(user));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john@example.com"))
                .andExpect(jsonPath("$[0].age").value(30));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        UserRequest request = new UserRequest();
        request.setName("John Updated");
        request.setEmail("john.updated@example.com");
        request.setAge(35);

        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setName(request.getName());
        response.setEmail(request.getEmail());
        response.setAge(request.getAge());

        when(userService.updateUser(anyLong(), any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Updated"))
                .andExpect(jsonPath("$.email").value("john.updated@example.com"))
                .andExpect(jsonPath("$.age").value(35));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(userService).deleteUser(1L);
    }
}