package org.example.homebankapp.service;

import lombok.extern.slf4j.Slf4j;
import org.example.homebankapp.dto.UpdateUserRequest;
import org.example.homebankapp.dto.UserDto;
import org.example.homebankapp.dto.UserMapper;
import org.example.homebankapp.exception.UserNotFoundException;
import org.example.homebankapp.model.User;
import org.example.homebankapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public UserDto create(UserDto userDto) {
        log.info("Creating user");
        User user = userMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserDto updateUser(String id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userMapper.updateEntity(userDto, existingUser);

        User updateUser = userRepository.save(existingUser);
        return userMapper.toDto(updateUser);

    }

    public UserDto partialUpdateUser(String id, UpdateUserRequest request) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

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

        return userMapper.toDto(userRepository.save(existingUser));
    }

    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}
