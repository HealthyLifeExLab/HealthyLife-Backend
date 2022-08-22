package com.exLab.healthyLife.service;

import com.exLab.healthyLife.service.dto.UserDto;

public interface UserService {

    UserDto getUserById (Long id);

    UserDto getUserByEmail (String email);

    UserDto saveUser (UserDto userDto);

    void deleteUser (Long id);

    boolean validateUser (String email, String password);
}
