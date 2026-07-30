package org.example.homebankapp.controller;

import org.example.homebankapp.dto.UserDto;
import org.example.homebankapp.service.UserService;
import org.example.homebankapp.util.UserTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApplicationController.class)
class ApplicationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void createUser_shouldReturnUser() throws Exception {
        UserDto userDto = UserTestUtil.validUserDto();

        when(userService.create(any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/bank/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UserTestUtil.toJson(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("June"))
                .andExpect(jsonPath("$.lastName").value("Doll"))
                .andExpect(jsonPath("$.email").value("june1x@gmail.com"))
                .andExpect(jsonPath("$.phoneNumber").value("0723498098"));
    }

    @Test
    void createUser_shouldReturnBadRequestWhenInputNotValid() throws Exception {
        UserDto invalidUserDto = UserTestUtil.userDtoWithBlankFirstName();

        when(userService.create(any(UserDto.class))).thenReturn(invalidUserDto);

        mockMvc.perform(post("/bank/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UserTestUtil.toJson(invalidUserDto))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() throws Exception {
        UserDto validUserDto = UserTestUtil.validUserDto();

        when(userService.getAllUsers()).thenReturn(List.of(validUserDto));

        mockMvc.perform(get("/bank/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("June"))
                .andExpect(jsonPath("$[0].lastName").value("Doll"))
                .andExpect(jsonPath("$[0].email").value("june1x@gmail.com"))
                .andExpect(jsonPath("$[0].phoneNumber").value("0723498098"));
    }

    @Test
    void updateUser_shouldReturnUpdateUser() throws Exception {
        UserDto validUserDto = UserTestUtil.validUserDto();
        UserDto res = new UserDto(
                "updated firstName",
                "updated lastName",
                "updated email",
                "updated phoneNumber"

        );
        when(userService.updateUser(eq("123"), any(UserDto.class))).thenReturn(res);

        mockMvc.perform(put("/bank/users/{id}", "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UserTestUtil.toJson(validUserDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("updated firstName"))
                .andExpect(jsonPath("$.lastName").value("updated lastName"))
                .andExpect(jsonPath("$.email").value("updated email"))
                .andExpect(jsonPath("$.phoneNumber").value("updated phoneNumber"));

        verify(userService).updateUser(eq("123"), any(UserDto.class));
    }

    @Test
    void updateUser_shouldReturnBadRequestWhenInputNotValid() throws Exception {
        mockMvc.perform(put("/bank/users/{id}", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(UserTestUtil.toJson(new UserDto("", "", "",""))))
                .andExpect(status().isBadRequest());
    }
}