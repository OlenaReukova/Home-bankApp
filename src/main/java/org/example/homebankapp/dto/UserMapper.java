package org.example.homebankapp.dto;

import org.example.homebankapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target= "id", ignore = true)
    @Mapping(source = "firstName" , target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "phoneNumber", target = "phoneNumber")

    User toEntity (UserDto dto);

    UserDto toDto(User user);

    @Mapping(target="id", ignore = true)
    void updateEntity(UserDto dto, @MappingTarget User user);
}
