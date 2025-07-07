package com.example.controller;

import com.example.dto.UserRequest;
import com.example.dto.UserResponse;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateUser() throws Exception {
        UserRequest request = new UserRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setAge(30);

        MvcResult result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        UserResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponse.class);

        assertEquals(request.getName(), response.getName());
        assertEquals(request.getEmail(), response.getEmail());
        assertEquals(request.getAge(), response.getAge());
        assertTrue(userRepository.existsById(response.getId()));
    }

    @Test
    void shouldReturnBadRequestForInvalidEmail() throws Exception {
        UserRequest request = new UserRequest();
        request.setName("John Doe");
        request.setEmail("invalid-email");
        request.setAge(30);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetUserById() throws Exception {
        UserRequest request = new UserRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setAge(30);

        MvcResult createResult = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        UserResponse createdUser = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), UserResponse.class);

        mockMvc.perform(get("/api/users/" + createdUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdUser.getId()))
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.email").value(request.getEmail()))
                .andExpect(jsonPath("$.age").value(request.getAge()));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        // Create user
        UserRequest createRequest = new UserRequest();
        createRequest.setName("John Doe");
        createRequest.setEmail("john@example.com");
        createRequest.setAge(30);

        MvcResult createResult = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn();

        UserResponse createdUser = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), UserResponse.class);

        // Update user
        UserRequest updateRequest = new UserRequest();
        updateRequest.setName("John Updated");
        updateRequest.setEmail("john.updated@example.com");
        updateRequest.setAge(35);

        mockMvc.perform(put("/api/users/" + createdUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdUser.getId()))
                .andExpect(jsonPath("$.name").value(updateRequest.getName()))
                .andExpect(jsonPath("$.email").value(updateRequest.getEmail()))
                .andExpect(jsonPath("$.age").value(updateRequest.getAge()));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        // Create user
        UserRequest request = new UserRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setAge(30);

        MvcResult createResult = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        UserResponse createdUser = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), UserResponse.class);

        // Delete user
        mockMvc.perform(delete("/api/users/" + createdUser.getId()))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/users/" + createdUser.getId()))
                .andExpect(status().isNotFound());
    }
}