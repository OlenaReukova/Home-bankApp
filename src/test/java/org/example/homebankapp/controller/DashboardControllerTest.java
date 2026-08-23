package org.example.homebankapp.controller;

import org.example.homebankapp.config.CustomUserDetailsService;
import org.example.homebankapp.config.SecurityConfig;
import org.example.homebankapp.controller.response.UserResponse;
import org.example.homebankapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@Import(SecurityConfig.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void dashboard_shouldReturnDashboardView_whenAuthenticated() throws Exception {
        when(userService.getUserByEmail("user@bank.com"))
                .thenReturn(new UserResponse("John", "Doe", "user@bank.com", "+44123456789"));

        mockMvc.perform(get("/dashboard").with(user("user@bank.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attribute("firstName", "John"))
                .andExpect(model().attribute("email", "user@bank.com"));
    }

    @Test
    void dashboard_shouldRedirectToLogin_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/dashboard").accept(org.springframework.http.MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
