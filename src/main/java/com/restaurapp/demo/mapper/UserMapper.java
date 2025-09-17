package com.restaurapp.demo.mapper;

import com.restaurapp.demo.domain.User;
import com.restaurapp.demo.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserDto toDto(User user);
    List<UserDto> toDtoList(List<User> users);
}
