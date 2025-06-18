package ru.practicum.shareit.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.gateway.client.UserClient;
import ru.practicum.shareit.gateway.dto.user.UserDto;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Тест для addUser
    @Test
    void testAddUser() throws Exception {
        UserDto userDto = new UserDto(null, "John Doe", "john.doe@example.com");

        UserDto expectedUserDto = new UserDto(1L, "John Doe", "john.doe@example.com");

        when(userClient.addUser(argThat(dto -> dto.getName().equals("John Doe") && dto.getEmail()
                .equals("john.doe@example.com"))))
                .thenReturn(ResponseEntity.status(201).body(expectedUserDto));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedUserDto.getId()))
                .andExpect(jsonPath("$.name").value(expectedUserDto.getName()))
                .andExpect(jsonPath("$.email").value(expectedUserDto.getEmail()));

        verify(userClient, times(1)).addUser(any(UserDto.class));
    }

    // Тест для updateUser
    @Test
    void testUpdateUser() throws Exception {
        Long userId = 1L;

        UserDto userDto = new UserDto(null, "Updated Name", "updated.email@example.com");

        UserDto expectedUserDto = new UserDto(userId, "Updated Name", "updated.email@example.com");

        when(userClient.updateUser(eq(userId), argThat(dto -> dto.getName().equals("Updated Name") && dto
                .getEmail().equals("updated.email@example.com"))))
                .thenReturn(ResponseEntity.ok(expectedUserDto));

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedUserDto.getId()))
                .andExpect(jsonPath("$.name").value(expectedUserDto.getName()))
                .andExpect(jsonPath("$.email").value(expectedUserDto.getEmail()));

        verify(userClient, times(1)).updateUser(eq(userId), any(UserDto.class));
    }

    // Тест для getUserById
    @Test
    void testGetUserById() throws Exception {
        Long userId = 1L;

        UserDto expectedUserDto = new UserDto(userId, "John Doe", "john.doe@example.com");

        when(userClient.getUserById(eq(userId)))
                .thenReturn(ResponseEntity.ok(expectedUserDto));

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedUserDto.getId()))
                .andExpect(jsonPath("$.name").value(expectedUserDto.getName()))
                .andExpect(jsonPath("$.email").value(expectedUserDto.getEmail()));

        verify(userClient, times(1)).getUserById(eq(userId));
    }

    // Тест для getAllUsers
    @Test
    void testGetAllUsers() throws Exception {
        UserDto user1 = new UserDto(1L, "User 1", "user1@example.com");
        UserDto user2 = new UserDto(2L, "User 2", "user2@example.com");

        Collection<UserDto> expectedUsers = Arrays.asList(user1, user2);

        when(userClient.getAllUsers())
                .thenReturn(ResponseEntity.ok(expectedUsers));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedUsers.size()))
                .andExpect(jsonPath("$[0].id").value(user1.getId()))
                .andExpect(jsonPath("$[0].name").value(user1.getName()))
                .andExpect(jsonPath("$[0].email").value(user1.getEmail()))
                .andExpect(jsonPath("$[1].id").value(user2.getId()))
                .andExpect(jsonPath("$[1].name").value(user2.getName()))
                .andExpect(jsonPath("$[1].email").value(user2.getEmail()));

        verify(userClient, times(1)).getAllUsers();
    }

    // Тест для deleteUserById
    @Test
    void testDeleteUserById() throws Exception {
        Long userId = 1L;

        when(userClient.deleteUserById(eq(userId))).thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNoContent());

        verify(userClient, times(1)).deleteUserById(eq(userId));
    }

    // Тест для deleteAllUsers
    @Test
    void testDeleteAllUsers() throws Exception {
        when(userClient.deleteAllUsers()).thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(delete("/users"))
                .andExpect(status().isNoContent());

        verify(userClient, times(1)).deleteAllUsers();
    }
}