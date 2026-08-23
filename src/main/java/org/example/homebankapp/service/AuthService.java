package org.example.homebankapp.service;

import lombok.extern.slf4j.Slf4j;
import org.example.homebankapp.controller.request.RegisterRequest;
import org.example.homebankapp.exception.EmailAlreadyExistsException;
import org.example.homebankapp.model.Role;
import org.example.homebankapp.model.User;
import org.example.homebankapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {
private final UserRepository userRepository;
private final PasswordEncoder passwordEncoder;

public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder){
    this.userRepository=userRepository;
    this.passwordEncoder=passwordEncoder;
}

public void register(RegisterRequest request){
    if(userRepository.findByEmail(request.email()).isPresent()){
        throw new EmailAlreadyExistsException(request.email());
    }

    User user =new User();
    user.setFirstName(request.firstName());
    user.setLastName(request.lastName());
    user.setEmail(request.email());
    user.setPhoneNumber(request.phoneNumber());
    user.setPassword(passwordEncoder.encode(request.password()));
    user.setRole(Role.USER);

    userRepository.save(user);
    log.info("New user registered: {}", request.email());
}
}
