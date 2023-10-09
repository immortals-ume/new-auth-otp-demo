package com.demo.auth.authdemoproject.mapper;


import com.demo.auth.authdemoproject.model.dto.LoginInfoDto;
import com.demo.auth.authdemoproject.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    User toUser(LoginInfoDto loginInfoDto);

}
