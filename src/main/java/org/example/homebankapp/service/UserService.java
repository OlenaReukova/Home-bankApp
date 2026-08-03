package org.example.homebankapp.service;

import lombok.extern.slf4j.Slf4j;
import org.example.homebankapp.dto.CreateUserRequest;
import org.example.homebankapp.dto.UpdateUserRequest;
import org.example.homebankapp.dto.UserMapper;
import org.example.homebankapp.dto.UserResponse;
import org.example.homebankapp.exception.NoChangesException;
import org.example.homebankapp.exception.UserNotFoundException;
import org.example.homebankapp.model.User;
import org.example.homebankapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponse createUser(CreateUserRequest createUserRequest) {
        log.info("Creating user");
        User user = userMapper.toEntity(createUserRequest);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponse updateUser(String id, CreateUserRequest createUserRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userMapper.updateEntity(createUserRequest, existingUser);

        User updateUser = userRepository.save(existingUser);
        return userMapper.toResponse(updateUser);

    }

    public UserResponse partialUpdateUser(String id, UpdateUserRequest request) {
        var existingUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        if (!StringUtils.hasLength(request.firstName())
                && !StringUtils.hasLength(request.lastName())
                && !StringUtils.hasLength(request.email())
                && !StringUtils.hasLength(request.phoneNumber())) {
            throw new NoChangesException();
        }

        if (request.firstName() != null && !request.firstName().isBlank()) {
            existingUser.setFirstName(request.firstName());
        }
        if (request.lastName() != null && !request.lastName().isBlank()) {
            existingUser.setLastName(request.lastName());
        }

        if (request.email() != null && !request.email().isBlank()) {
            existingUser.setEmail(request.email());
        }

        if (request.phoneNumber() != null && !request.phoneNumber().isBlank()) {
            existingUser.setPhoneNumber(request.phoneNumber());
        }

        return userMapper.toResponse(userRepository.save(existingUser));
    }

    public void deleteUser(String id) {
        log.info("Deleting user id={}", id);
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}
