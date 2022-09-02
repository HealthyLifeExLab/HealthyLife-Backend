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
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findUserByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NullResultException("There is no user with id " + id));
        return toDto(user);
    }

    private UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getPassword());
        return userDto;
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findUserByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new NullResultException("There is no user with email " + email));
        return toDto(user);
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        try {
            if(!correctPassword(userDto.getPassword())||!correctEmail(userDto.getEmail())){
                throw new CreatingException("Incorrect password or email");
            }
            User createdUser = userRepository.save(toUser(userDto));
            UserDto createdUserDto = toDto(createdUser);
            return getUserById(createdUserDto.getId());
        } catch (CreatingException e){
            throw new CreatingException("Incorrect password or email");
        } catch (RuntimeException e) {
            throw new CreatingException("The user is not created");
        }
    }

    public User toUser(UserDto userDto) {
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
        } catch (RuntimeException e) {
            throw new DeleteException("The user with id " + id + " is not deleted");
        }
    }

    @Override
    public boolean validateUser(String email, String password) {
        UserDto userDto = getUserByEmail(email);
        if (userDto == null) {
            return false;
        }
        String passwordHash = EncryptorUtil.encrypt(password);
        return userDto.getPassword().equals(passwordHash);
    }

    @Override
    public boolean validateEmail(String email) {
        try {
            getUserByEmail(email);
            return true;
        } catch (NullResultException e) {
            return false;
        }
    }

    private boolean correctPassword(String password) {
        return password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20}$");
    }

    private boolean correctEmail(String email) {
        return email.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"" +
                "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c" +
                "\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?" +
                "|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|" +
                "[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09" +
                "\\x0b\\x0c\\x0e-\\x7f])+)\\])");
    }
}
