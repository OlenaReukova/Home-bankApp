package org.example.homebankapp.config;

import org.example.homebankapp.model.Role;
import org.example.homebankapp.model.User;
import org.example.homebankapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsService service;

    @BeforeEach
    void setUp() {
        service = new CustomUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        User user = new User();
        user.setEmail("user@bank.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);

        when(userRepository.findByEmail("user@bank.com")).thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername("user@bank.com");

        assertEquals("user@bank.com", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));

        verify(userRepository).findByEmail("user@bank.com");
    }

    @Test
    void loadUserByUsername_shouldReturnAdminRole_whenUserIsAdmin() {
        User admin = new User();
        admin.setEmail("admin@bank.com");
        admin.setPassword("encodedPassword");
        admin.setRole(Role.ADMIN);

        when(userRepository.findByEmail("admin@bank.com")).thenReturn(Optional.of(admin));

        UserDetails result = service.loadUserByUsername("admin@bank.com");

        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_shouldThrow_whenUserNotFound() {
        when(userRepository.findByEmail("notfound@bank.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("notfound@bank.com"));

        verify(userRepository).findByEmail("notfound@bank.com");
    }
}
