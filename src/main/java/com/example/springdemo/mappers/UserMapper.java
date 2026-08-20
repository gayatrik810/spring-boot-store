package com.example.springdemo.mappers;

import com.example.springdemo.dtos.RegisterUserRequest;
import com.example.springdemo.dtos.UpdateUserRequest;
import com.example.springdemo.dtos.UserDto;
import com.example.springdemo.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    // @Mapping(target = "createdAt" , expression = "java(java.time.LocalDateTime.now())")
    UserDto toDto(User user);
    User toEntity(RegisterUserRequest request);

    void update(UpdateUserRequest request, @MappingTarget User user);
}