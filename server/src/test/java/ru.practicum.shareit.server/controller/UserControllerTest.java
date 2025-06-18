package ru.practicum.shareit.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.dto.user.UserDto;
import ru.practicum.shareit.server.service.user.UserService;

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
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Тест для addUser
    @Test
    void testAddUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        UserDto createdUser = UserDto.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        when(userService.addUser(eq(userDto))).thenReturn(createdUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(createdUser.getId()))
                .andExpect(jsonPath("$.name").value(createdUser.getName()))
                .andExpect(jsonPath("$.email").value(createdUser.getEmail()));

        verify(userService, times(1)).addUser(eq(userDto));
    }

    @Test
    void testUpdateUser() throws Exception {
        Long userId = 1L;

        // DTO для отправки в запросе (без id)
        UserDto updateDto = UserDto.builder()
                .name("Updated Name")
                .email("updated.email@example.com")
                .build();

        // DTO с id, которое будет передано в сервис
        UserDto dtoWithId = UserDto.builder()
                .id(userId)
                .name("Updated Name")
                .email("updated.email@example.com")
                .build();

        // DTO, которое возвращает сервис
        UserDto updatedUser = UserDto.builder()
                .id(userId)
                .name("Updated Name")
                .email("updated.email@example.com")
                .build();

        // Мокируем сервис, чтобы он ожидал DTO с id
        when(userService.updateUser(eq(userId), eq(dtoWithId))).thenReturn(updatedUser);

        // Выполняем PATCH-запрос
        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedUser.getId()))
                .andExpect(jsonPath("$.name").value(updatedUser.getName()))
                .andExpect(jsonPath("$.email").value(updatedUser.getEmail()));

        // Проверяем, что метод сервиса был вызван один раз с правильными аргументами
        verify(userService, times(1)).updateUser(eq(userId), eq(dtoWithId));
    }

    // Тест для getUserById
    @Test
    void testGetUserById() throws Exception {
        Long userId = 1L;

        UserDto expectedUser = UserDto.builder()
                .id(userId)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        when(userService.getUserById(eq(userId))).thenReturn(expectedUser);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedUser.getId()))
                .andExpect(jsonPath("$.name").value(expectedUser.getName()))
                .andExpect(jsonPath("$.email").value(expectedUser.getEmail()));

        verify(userService, times(1)).getUserById(eq(userId));
    }

    // Тест для getAllUsers
    @Test
    void testGetAllUsers() throws Exception {
        UserDto user1 = UserDto.builder()
                .id(1L)
                .name("User 1")
                .email("user1@example.com")
                .build();

        UserDto user2 = UserDto.builder()
                .id(2L)
                .name("User 2")
                .email("user2@example.com")
                .build();

        Collection<UserDto> expectedUsers = Arrays.asList(user1, user2);

        when(userService.getAllUsers()).thenReturn(expectedUsers);

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

        verify(userService, times(1)).getAllUsers();
    }

    // Тест для deleteUserById
    @Test
    void testDeleteUserById() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUserById(eq(userId));
    }

    // Тест для deleteAllUsers
    @Test
    void testDeleteAllUsers() throws Exception {
        mockMvc.perform(delete("/users"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteAllUsers();
    }
}