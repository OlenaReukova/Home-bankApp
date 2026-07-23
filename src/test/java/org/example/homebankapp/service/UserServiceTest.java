package org.example.homebankapp.service;

import org.example.homebankapp.dto.UserDto;
import org.example.homebankapp.dto.UserMapper;
import org.example.homebankapp.model.User;
import org.example.homebankapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, userMapper);
    }

    @Test
    void create_shouldReturnUser() {
        UserDto userDto = new UserDto("June", "Doll", "june1x@gmail.com", "0723498098");
        User user = userMapper.toEntity(userDto);

        when(userRepository.save(user)).thenReturn(user);

        UserDto result = userService.create(userDto);

        assertNotNull(result);
        assertEquals("June", result.firstName());
        assertEquals("Doll", result.lastName());
    }
}