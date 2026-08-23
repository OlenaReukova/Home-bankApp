package org.example.homebankapp.dto;

import org.example.homebankapp.controller.request.CreateUserRequest;
import org.example.homebankapp.controller.response.UserResponse;
import org.example.homebankapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target= "role", ignore = true)
    @Mapping(target= "password", ignore = true)
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(CreateUserRequest dto);

    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target= "role", ignore = true)
    @Mapping(target= "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(CreateUserRequest dto, @MappingTarget User user);
}
