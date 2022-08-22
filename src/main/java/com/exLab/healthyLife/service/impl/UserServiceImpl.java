package com.exLab.healthyLife.service.impl;

import com.exLab.healthyLife.dao.entity.User;
import com.exLab.healthyLife.dao.repository.UserRepository;
import com.exLab.healthyLife.service.UserService;
import com.exLab.healthyLife.service.dto.UserDto;
import com.exLab.healthyLife.service.exceptions.CreatingException;
import com.exLab.healthyLife.service.exceptions.DeleteException;
import com.exLab.healthyLife.service.exceptions.NullResultException;
import com.exLab.healthyLife.service.util.EncryptorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new NullResultException("There is no user with id " + id));
        return toDto(user);
    }

    private UserDto toDto(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getPassword());
        return userDto;
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new NullResultException("There is no user with email " + email));
        return toDto(user);
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        try {
            User createdUser = userRepository.save(toUser(userDto));
            UserDto createdUserDto = toDto(createdUser);
            return getUserById(createdUserDto.getId());
        } catch (RuntimeException e){
            throw new CreatingException("The user is not created");
        }
    }

    public User toUser (UserDto userDto){
        User user = new User();
        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());
        user.setPassword(EncryptorUtil.encrypt(userDto.getPassword()));
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        try {
            userRepository.softDelete(id);
        } catch (RuntimeException e){
            throw new DeleteException("The user with id " + id + " is not deleted");
        }
    }

    @Override
    public boolean validateUser(String email, String password) {
        UserDto userDto = getUserByEmail(email);
        if (userDto == null){
            return false;
        }
        String passwordHash = EncryptorUtil.encrypt(password);
        return userDto.getPassword().equals(passwordHash);
    }
}
