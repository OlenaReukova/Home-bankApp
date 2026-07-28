package org.example.homebankapp.util;

import org.example.homebankapp.dto.UserDto;
import org.example.homebankapp.model.User;
import tools.jackson.databind.ObjectMapper;

public class UserTestUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    private UserTestUtil(){};

    public static UserDto validUserDto(){
        return new UserDto("June", "Doll", "june1x@gmail.com", "0723498098");
    }

    public static UserDto anotherValidUserDto(){
        return new UserDto("Tom", "Wills", "tom2@gmail.com", "0723498666");
    }

    public static UserDto userDtoWithBlankFirstName(){
        return new UserDto("", "Doll", "june1x@gmail.com", "0723498098");
    };

    public static User validUser() {
        return new User(null,
                "June",
                "Doll",
                "june1x@gmail.com",
                "0723498098");
    }

    public static String toJson(Object object){
        try {
            return mapper.writeValueAsString(object);
        }catch (Exception e) {
        throw new RuntimeException(e);
        }
    }
}
