package org.example.homebankapp.controller;

import org.example.homebankapp.dto.UserDto;
import org.example.homebankapp.model.User;
import org.example.homebankapp.repository.UserRepository;
import org.example.homebankapp.util.UserTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@AutoConfigureTestRestTemplate
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationControllerIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    private String url(String path) {
        return "http://localhost:" + port + "/bank/user" + path;
    }

    @Test
    void create_thenReturnNewUser() {
        UserDto newUserDto = UserTestUtil.validUserDto();
        String jsonUser = UserTestUtil.toJson(newUserDto);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(jsonUser, headers);

        ResponseEntity<UserDto> response = restTemplate.postForEntity(url(""), request, UserDto.class);
        System.out.println("STATUS = " + response.getStatusCode());
        System.out.println("BODY   = " + response.getBody());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        UserDto body = response.getBody();

        assertEquals("June", body.firstName());
        assertEquals("Doll", body.lastName());
        assertEquals("june1x@gmail.com", body.email());
        assertEquals("0723498098", body.phoneNumber());

        assertEquals(1, userRepository.count());

        User saved = userRepository.findAll().getFirst();
        assertEquals("June", saved.getFirstName());
    }
}
