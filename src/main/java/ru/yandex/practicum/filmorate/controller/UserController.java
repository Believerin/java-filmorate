package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;
    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Integer id) {
        return userStorage.getUser(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return  userStorage.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userStorage.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public List<User> addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        if (id <= 0 && friendId <= 0) {
            throw new NullPointerException(id < 0 & friendId < 0 ? "id и friendId" : id < 0 ? "id" : "friendId");
        }
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public List<User> deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        if (id <= 0 && friendId <= 0) {
            throw new NullPointerException(id < 0 & friendId < 0 ? "id и friendId" : id < 0 ? "id" : "friendId");
        }
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Set<User> getAllFriends(@PathVariable Integer id) {
        if (id <= 0) {
            throw new NullPointerException("id");
        }
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        if (id <= 0 && otherId <= 0) {
            throw new NullPointerException(id < 0 & otherId < 0 ? "id и otherId" : id < 0 ? "id" : "otherId");
        }
        return userService.getCommonFriends(id, otherId);
    }
}