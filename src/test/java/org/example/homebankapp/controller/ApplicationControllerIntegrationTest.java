package org.example.homebankapp.controller;

import org.example.homebankapp.dto.UpdateUserRequest;
import org.example.homebankapp.dto.UserDto;
import org.example.homebankapp.model.User;
import org.example.homebankapp.repository.UserRepository;
import org.example.homebankapp.util.UserTestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestRestTemplate
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationControllerIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    private String url(String path) {
        return "http://localhost:" + port + "/bank/users" + path;
    }

    @Test
    void create_thenReturnNewUser() {
        long current = userRepository.count();
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

        assertEquals(current + 1, userRepository.count());

        User saved = userRepository.findAll().getLast();
        assertEquals("June", saved.getFirstName());
    }

    @Test
    void updateUser_shouldUpdateExistingUser() throws Exception {
        User existingUser = UserTestUtil.validUser();
        User savedUser = userRepository.save(existingUser);

        UserDto updateDto = UserTestUtil.anotherValidUserDto();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(
                UserTestUtil.toJson(updateDto),
                headers
        );

        ResponseEntity<UserDto> response = restTemplate.exchange(
                url("/" + savedUser.getId()),
                HttpMethod.PUT,
                request,
                UserDto.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        UserDto body = response.getBody();

        assertEquals("Tom", body.firstName());
        assertEquals("Wills", body.lastName());
        assertEquals("tom2@gmail.com", body.email());
        assertEquals("0723498666", body.phoneNumber());

        User updatedUser = userRepository.findById(savedUser.getId())
                .orElseThrow();

        assertEquals("Tom", updatedUser.getFirstName());
        assertEquals("Wills", updatedUser.getLastName());
        assertEquals("tom2@gmail.com", updatedUser.getEmail());
        assertEquals("0723498666", updatedUser.getPhoneNumber());
    }

    @Test
    void partialUpdateUser_shouldUpdateOnlyProvidedFields() {

        User existingUser = UserTestUtil.validUser();
        User savedUser = userRepository.save(existingUser);

        UpdateUserRequest request = new UpdateUserRequest(
                "Tom",
                null,
                "updated@gmail.com",
                null
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(
                UserTestUtil.toJson(request),
                headers
        );

        ResponseEntity<UserDto> response = restTemplate.exchange(
                url("/" + savedUser.getId()),
                HttpMethod.PATCH,
                entity,
                UserDto.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        UserDto body = response.getBody();

        assertEquals("Tom", body.firstName());
        assertEquals("Doll", body.lastName());
        assertEquals("updated@gmail.com", body.email());
        assertEquals("0723498098", body.phoneNumber());

        User updatedUser = userRepository.findById(savedUser.getId())
                .orElseThrow();

        assertEquals("Tom", updatedUser.getFirstName());
        assertEquals("Doll", updatedUser.getLastName());
        assertEquals("updated@gmail.com", updatedUser.getEmail());
        assertEquals("0723498098", updatedUser.getPhoneNumber());
    }

    @Test
    void partialUpdateUser_shouldReturnNotFoundWhenUserDoesNotExist() {

        UpdateUserRequest request = new UpdateUserRequest(
                "Tom",
                null,
                null,
                null
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(
                UserTestUtil.toJson(request),
                headers
        );

        ResponseEntity<String> response = restTemplate.exchange(
                url("/non-existent-id"),
                HttpMethod.PATCH,
                entity,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody()
                .contains("User not found with id: non-existent-id"));
    }

    @Test
    void deleteUser_shouldRemoveUserAndReturnNoContent() {
        long current = userRepository.count();

        User existingUser = UserTestUtil.validUser();
        User savedUser = userRepository.save(existingUser);

        assertEquals(current + 1, userRepository.count());

        ResponseEntity<Void> response = restTemplate.exchange(
                url("/" + savedUser.getId()),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        assertTrue(userRepository.findById(savedUser.getId()).isEmpty());
    }

    @Test
    void deleteUser_shouldReturnNotFoundWhenUserDoesNotExist() {

        ResponseEntity<String> response = restTemplate.exchange(
                url("/non-existent-id"),
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody()
                .contains("User not found with id: non-existent-id"));
    }

}
