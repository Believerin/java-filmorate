package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NoSuchBodyException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;
    @Autowired
    public UserController( @Qualifier("Secondary") UserService userService) {
        this.userService = userService;
    }

    @DeleteMapping("/{userId}")
    public User delete(@PathVariable Integer userId) {
        return userService.delete(userId);
    }

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return  userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public List<User> addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        if (id <= 0 || friendId <= 0) {
            throw new NoSuchBodyException(id < 0 & friendId < 0 ? "id пользователя и friendId друга"
                    : id < 0 ? "id пользователя" : "friendId друга");
        }
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public List<User> deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        if (id <= 0 || friendId <= 0) {
            throw new NoSuchBodyException(id < 0 & friendId < 0 ? "id пользователя и friendId друга"
                    : id < 0 ? "id пользователя" : "friendId друга");
        }
        return userService.deleteFriend(id, friendId);
    }


    @GetMapping("/{id}/friends")
    public Set<User> getAllFriends(@PathVariable Integer id) {
        if (id <= 0) {
            throw new NoSuchBodyException("id пользователя");
        }
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        if (id <= 0 || otherId <= 0) {
            throw new NoSuchBodyException(id < 0 & otherId < 0 ? "id 1-го пользователя и otherId 2-го пользователя":
                    id < 0 ? "id 1-го пользователя" : "otherId 2-го пользователя");
        }
        return userService.getCommonFriends(id, otherId);
    }
}