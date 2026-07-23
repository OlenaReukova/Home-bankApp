package org.example.homebankapp.controller;

import org.example.homebankapp.dto.UserDto;
import org.example.homebankapp.service.UserService;
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
        UserDto userDto = new UserDto("June", "Doll", "june1x@gmail.com", "0723498098");

        when(userService.create(any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/bank/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        """ 
                                                          {
                                                          "firstName":"June",
                                                          "lastName":"Doll",
                                                          "email":"june1x@gmail.com",
                                                          "phoneNumber":"0723498098"}
                                """
                )
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("June"))
                .andExpect(jsonPath("$.lastName").value("Doll"))
                .andExpect(jsonPath("$.email").value("june1x@gmail.com"))
                .andExpect(jsonPath("$.phoneNumber").value("0723498098"));
    }

    @Test
    void createUser_ShouldReturnBadRequestWhenInputNotValid() throws Exception{
        mockMvc.perform(post("/bank/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        """ 
                                                          {
                                                          "firstName": null,
                                                          "lastName":"Doll",
                                                          "email":"june1x@gmail.com",
                                                          "phoneNumber":"0723498098"}
                                """
                )
        )
                .andExpect(status().isBadRequest());
    }

}