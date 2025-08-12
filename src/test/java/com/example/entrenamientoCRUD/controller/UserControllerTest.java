package com.example.entrenamientoCRUD.controller;

import com.example.entrenamientoCRUD.model.User;
import com.example.entrenamientoCRUD.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper; // Para convertir objetos a JSON

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc; // Principal herramienta para simular peticiones
import org.springframework.test.web.servlet.setup.MockMvcBuilders; // Para configuración manual si es necesario

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; // POST, GET, PUT, DELETE
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*; // status, jsonPath



@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private User updateUser;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "testUser", "testuserLastName", "hashedPassword123",
                "test@test.com");
        updateUser = new User(1L, "updateUser", "updateUserLastName", "newHashedPassword456",
                "updateTest@test.com");
    }

    @Test
    void createdUser_shouldReturnCreatedUser() throws Exception{
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        String userJson = objectMapper.writeValueAsString(new User(null, "testuser",
                "testuserLastName", "rawPassword", "test@test.com"));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("testuser"))
                .andExpect(jsonPath("$.lastname").value("testuserlastname"))
                .andExpect(jsonPath("$.email").value(("test@test.com")));
    }

    @Test
    void getUserById_shouldResturnUser() throws Exception {

        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/api/users/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("username"));
    }

    @Test
    void getUserById_shouldReruntNotFound() throws Exception {
        when(userService.getUserById(2L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/2")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_shouldReturnListofUsers() throws Exception {
        User user2 = new User(2L, "anotheruser", "anoterUserLastName", "pass123",
                "test2@test.com");
        when(userService.getAllUsers()).thenReturn(Arrays.asList(testUser, user2));

        mockMvc.perform(get("/api/users")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length").value(2))
                .andExpect(jsonPath("$[0].name").value("testUser"))
                .andExpect(jsonPath("$[1].name").value("anoterUserLastName"));
    }

    @Test
    void updateUser_shouldReturnUpdateuser() throws Exception {
        when(userService.updateUser(any(Long.class), any(User.class))).thenReturn(updateUser);

        String updateuserjson = objectMapper.writeValueAsString(new User(null, "updateUser",
                "updateUserLastName", "newPass", "updateTest@test.com"));

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateuserjson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("updateUser"))
                .andExpect(jsonPath("$.email").value("updateTest@test.com"));
    }

    @Test
    void updateUser_shouldReturnNotFound() throws Exception {
        when(userService.updateUser(any(Long.class), any(User.class)))
                .thenThrow(new RuntimeException("El usuario con id: " + any(Long.class) + " no existe."));

        String userJson = objectMapper.writeValueAsString(new User(null, "noextste", "noexiste",
                "pass", "non@non.com"));

        mockMvc.perform(put("/api/users/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void seleteUser_shouldReturnNotFound() throws Exception {
        doNothing().when(userService).deleteUser(99L);
        /*when(userService.deleteUser(99L))
                .thenThrow(new RuntimeException("El usuario con id: " + any(Long.class) + " no existe."));*/
    }
}
