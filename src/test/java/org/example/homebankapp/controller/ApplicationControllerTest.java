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

import static org.mockito.ArgumentMatchers.any;
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

        mockMvc.perform(post("/bank/user")
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

        mockMvc.perform(post("/bank/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UserTestUtil.toJson(invalidUserDto))
                )
                .andExpect(status().isBadRequest());
    }

}