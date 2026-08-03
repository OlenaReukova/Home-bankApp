package org.example.homebankapp.util;

import org.example.homebankapp.controller.request.CreateUserRequest;
import org.example.homebankapp.controller.response.UserResponse;
import org.example.homebankapp.model.User;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

public class UserTestUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    private UserTestUtil() {
    }

    ;

    public static CreateUserRequest validUserDto() {
        return new CreateUserRequest("June", "Doll", "june1x@gmail.com", "0723498098");
    }

    public static CreateUserRequest anotherValidUserDto() {
        return new CreateUserRequest("Tom", "Wills", "tom2@gmail.com", "0723498666");
    }

    public static CreateUserRequest userDtoWithBlankFirstName() {
        return new CreateUserRequest("", "Doll", "june1x@gmail.com", "0723498098");
    }

    ;

    public static User validUser() {
        return new User(null,
                "June",
                "Doll",
                "june1x@gmail.com",
                "0723498098",
                LocalDateTime.of(2026, 04, 3, 10, 15, 30),
                LocalDateTime.of(2026, 05, 3, 10, 15, 30)
        );
    }

    public static UserResponse validUserResponse() {
        return new UserResponse(
                "June",
                "Doll",
                "june1x@gmail.com",
                "0723498098"
        );
    }

    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
