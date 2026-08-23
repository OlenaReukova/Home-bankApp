package org.example.homebankapp.config;

import lombok.extern.slf4j.Slf4j;
import org.example.homebankapp.model.Role;
import org.example.homebankapp.model.User;
import org.example.homebankapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Configuration
public class AdminSeedConfig {
    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    @Value("${ADMIN_FIRST_NAME}")
    private String adminFirstName;

    @Value("${ADMIN_LAST_NAME}")
    private String adminLastName;

    @Bean
    public CommandLineRunner seedAdmin(UserRepository userRepository, PasswordEncoder encoder){
        return args -> {
            if (userRepository.findByEmail(adminEmail).isEmpty()){
                User admin = new User();
                admin.setFirstName(adminFirstName);
                admin.setLastName(adminLastName);
                admin.setEmail(adminEmail);
                admin.setPhoneNumber("+0000");
                admin.setPassword(encoder.encode(adminPassword));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
                log.info("Admin user seeded: {}", adminEmail);
            }else{
                log.info("Admin user already exists");
            }
        };
    }

}
