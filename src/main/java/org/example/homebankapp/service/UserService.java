package org.example.homebankapp.service;

import lombok.AllArgsConstructor;
import org.example.homebankapp.dto.UserDto;
import org.example.homebankapp.dto.UserMapper;
import org.example.homebankapp.model.User;
import org.example.homebankapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper){
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public User create(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        return userRepository.save(user);
    }
}
