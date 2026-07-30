package org.example.homebankapp.service;

import org.example.homebankapp.dto.UserDto;
import org.example.homebankapp.dto.UserMapper;
import org.example.homebankapp.exception.UserNotFoundException;
import org.example.homebankapp.model.User;
import org.example.homebankapp.repository.UserRepository;
import org.example.homebankapp.util.UserTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @Test
    void getAllUsers_shouldReturnListOfUsers() {
        when(userRepository.findAll()).thenReturn(List.of(UserTestUtil.validUser()));

        List<UserDto> allUsers = userService.getAllUsers();

        assertNotNull(allUsers);
        assertEquals(1, allUsers.size());

        UserDto result = allUsers.get(0);

        assertEquals("June", result.firstName());
        assertEquals("Doll", result.lastName());
        assertEquals("june1x@gmail.com", result.email());
        assertEquals("0723498098", result.phoneNumber());
    }

    @Test
    void updateUser_shouldUpdateAndReturnUser() {
        User existingUser = UserTestUtil.validUser();
        existingUser.setId("1001");

        UserDto updateDto = UserTestUtil.anotherValidUserDto();

        when(userRepository.findById("1001")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto result = userService.updateUser("1001", updateDto);

        assertEquals("Tom", result.firstName());
        assertEquals("Wills", result.lastName());

        verify(userRepository).save(existingUser);
    }

    @Test
    void updateUser_shouldThrowWhenUserIdNotFound() {
        String invalidId = "non-existent-id";
        UserDto updateDto = UserTestUtil.anotherValidUserDto();

        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.updateUser(invalidId, updateDto));

        verify(userRepository).findById(invalidId);
        verify(userRepository, never()).save(any(User.class));
    }
}