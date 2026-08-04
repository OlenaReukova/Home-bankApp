package org.example.homebankapp.service;

import org.example.homebankapp.controller.request.CreateUserRequest;
import org.example.homebankapp.controller.request.UpdateUserRequest;
import org.example.homebankapp.controller.response.UserResponse;
import org.example.homebankapp.dto.UserMapper;
import org.example.homebankapp.exception.NoChangesException;
import org.example.homebankapp.exception.UserNotFoundException;
import org.example.homebankapp.model.User;
import org.example.homebankapp.repository.UserRepository;
import org.example.homebankapp.util.UserTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @Mock
    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, userMapper);
    }

    @Test
    void create_shouldReturnUser() {
        CreateUserRequest createUserRequest = new CreateUserRequest("June", "Doll", "june1x@gmail.com", "0723498098");
        User user = userMapper.toEntity(createUserRequest);

        when(userRepository.save(user)).thenReturn(user);

        UserResponse result = userService.createUser(createUserRequest);

        assertNotNull(result);
        assertEquals("June", result.firstName());
        assertEquals("Doll", result.lastName());
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() {
        when(userRepository.findAll()).thenReturn(List.of(UserTestUtil.validUser()));

        List<UserResponse> allUsers = userService.getAllUsers();

        assertNotNull(allUsers);
        assertEquals(1, allUsers.size());

        UserResponse result = allUsers.get(0);

        assertEquals("June", result.firstName());
        assertEquals("Doll", result.lastName());
        assertEquals("june1x@gmail.com", result.email());
        assertEquals("0723498098", result.phoneNumber());
    }

    @Test
    void updateUser_shouldUpdateAndReturnUser() {
        User existingUser = UserTestUtil.validUser();
        existingUser.setId("1001");

        CreateUserRequest updateDto = UserTestUtil.anotherValidUserDto();

        when(userRepository.findById("1001")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse result = userService.updateUser("1001", updateDto);

        assertEquals("Tom", result.firstName());
        assertEquals("Wills", result.lastName());

        verify(userRepository).save(existingUser);
    }

    @Test
    void updateUser_shouldThrowWhenUserIdNotFound() {
        String invalidId = "non-existent-id";
        CreateUserRequest updateDto = UserTestUtil.anotherValidUserDto();

        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.updateUser(invalidId, updateDto));

        verify(userRepository).findById(invalidId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void partialUpdateUser_shouldUpdateOnlyProvidedFields() {


        User existingUser = UserTestUtil.validUser();
        existingUser.setId("1001");

        UpdateUserRequest request = new UpdateUserRequest(
                "Tom",
                null,
                "tom.updated@gmail.com",
                null
        );

        when(userRepository.findById("1001"))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse result = userService.partialUpdateUser("1001", request);

        assertEquals("Tom", result.firstName());
        assertEquals("Doll", result.lastName()); // unchanged
        assertEquals("tom.updated@gmail.com", result.email());
        assertEquals("0723498098", result.phoneNumber()); // unchanged

        assertEquals("Tom", existingUser.getFirstName());
        assertEquals("Doll", existingUser.getLastName());
        assertEquals("tom.updated@gmail.com", existingUser.getEmail());
        assertEquals("0723498098", existingUser.getPhoneNumber());

        verify(userRepository).save(existingUser);
    }

    @Test
    void partialUpdateUser_shouldUpdateAllAttributes() {
        User existingUser = UserTestUtil.validUser();
        existingUser.setId("1001");

        var request = new UpdateUserRequest("Tom", "Zencke", "tom.updated@gmail.com", "142554");

        when(userRepository.findById("1001")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = userService.partialUpdateUser("1001", request);

        var expectedUser = new UserResponse("Tom", "Zencke", "tom.updated@gmail.com", "142554");
        assertEquals(expectedUser, result, "All attributes are changed");

        verify(userRepository).save(existingUser);
    }

    @Test
    @DisplayName("Given all attributes When all are null Then throws exception")
    void partialUpdateUser_shouldThrow() {
        User existingUser = UserTestUtil.validUser();
        existingUser.setId("1001");

        var request = new UpdateUserRequest(null, null, null, null);
        when(userRepository.findById("1001")).thenReturn(Optional.of(existingUser));

        assertThrows(NoChangesException.class, () -> userService.partialUpdateUser("1001", request));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Given all attributes When empty Then throws exception")
    void partialUpdateUser_given_empty_shouldThrow() {
        User existingUser = UserTestUtil.validUser();
        existingUser.setId("1001");

        UpdateUserRequest request = new UpdateUserRequest(
                "",
                "",
                "",
                ""
        );

        when(userRepository.findById("1001")).thenReturn(Optional.of(existingUser));

        assertThrows(NoChangesException.class, () -> userService.partialUpdateUser("1001", request));
        verify(userRepository, never()).save(any());
    }


    @Test
    void deleteUser_shouldDeleteById_whenValidId() {
        when(userRepository.existsById(anyString()))
                .thenReturn(true);
        doNothing().when(userRepository).deleteById(anyString());

        userService.deleteUser("abc-123");

        verify(userRepository, times(1))
                .deleteById(anyString());
    }

    @Test
    void deleteUser_shouldThrowException_whenInvalidId() {
        when(userRepository.existsById(anyString()))
                .thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser("abc-123"));
    }

    @Test
    void getUserById_shouldReturnUser() {
        User user = UserTestUtil.validUser();
        user.setId("1001");

        when(userRepository.findById("1001"))
                .thenReturn(Optional.of(user));

        UserResponse result = userService.getUserById("1001");

        assertNotNull(result);
        assertEquals("June", result.firstName());
        assertEquals("Doll", result.lastName());
        assertEquals("june1x@gmail.com", result.email());
        assertEquals("0723498098", result.phoneNumber());

        verify(userRepository).findById("1001");
    }

    @Test
    void getUserById_shouldThrowWhenUserNotFound() {
        String id = "1001";

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserById(id)
        );
        verify(userRepository).findById(id);
    }

}