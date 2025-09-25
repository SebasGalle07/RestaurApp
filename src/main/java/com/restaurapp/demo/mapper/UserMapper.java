package com.restaurapp.demo.mapper;

import com.restaurapp.demo.domain.Role;
import com.restaurapp.demo.domain.User;
import com.restaurapp.demo.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    // Ignoramos passwordHash en el DTO (no se expone)
    @Mapping(target = "rol", expression = "java(map(user.getRol()))")
    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> users);

    // Helpers para MapStruct
    default String map(Role role) {
        return role == null ? null : role.getValue(); // "admin", "mesero", ...
    }
}
