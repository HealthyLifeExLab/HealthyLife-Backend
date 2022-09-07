package com.exLab.healthyLife.controller;

import com.exLab.healthyLife.service.UserService;
import com.exLab.healthyLife.service.dto.UserDto;
import com.exLab.healthyLife.service.exceptions.CreatingException;
import com.exLab.healthyLife.service.exceptions.NullResultException;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity registerUser(@RequestBody UserDto userDto) {
        try {
            UserDto savedUser = userService.saveUser(userDto);
            return new ResponseEntity<>(savedUser.getId(), HttpStatus.CREATED);
        } catch (CreatingException e) {
            if (userService.validateEmail(userDto.getEmail())) {
                return new ResponseEntity<>("User with this email exists", HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }

    @PostMapping("/login")
    public ResponseEntity loginUser(@RequestBody UserDto userDto) {
        try {
            if (userService.validateUser(userDto.getEmail(), userDto.getPassword())) {
                UserDto loggedUser = userService.getUserByEmail(userDto.getEmail());
                return new ResponseEntity<>(loggedUser.getId(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Wrong email or password", HttpStatus.BAD_REQUEST);
            }
        } catch (NullResultException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity getUser(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
        } catch (NullResultException e) {
            return new ResponseEntity<>("There is no user with id " + id, HttpStatus.BAD_REQUEST);
        }
    }
}
