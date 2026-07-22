package org.example.homebankapp.dto;

import org.example.homebankapp.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target= "id", ignore = true)
    @Mapping(source = "firstName" , target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "balance", target = "balance")

    User toEntity (UserDto dto);

    UserDto toDto(User user);
}
