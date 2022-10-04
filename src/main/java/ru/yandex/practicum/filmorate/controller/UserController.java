package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.*;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Slf4j
@Getter
public class UserController {

    Predicate<User> requirementsPassed = user -> !user.getLogin().isBlank() // Для тестов (в них @Valid не работала)
            && !user.getLogin().contains(" ")
            && !user.getEmail().isBlank()
            && user.getEmail().contains("@")
            && user.getBirthday().isBefore(LocalDate.now());

    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if (requirementsPassed.test(user)) {
            List<String> logins = users.values().stream()
                    .map(User::getLogin)
                    .collect(Collectors.toList());
            if (logins.contains(user.getLogin())) {
                throw new ValidationException("Пользователь уже существует");
            }
            if (user.getName()==null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            user.setId();
            users.put(user.getId(), user);
            return user;
        } else {
            throw new ValidationException("Данные пользователя не соответствуют критериям");
        }
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (requirementsPassed.test(user)) {
            if (user.getName()==null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            if (users.containsKey(user.getId())) {
                users.put(user.getId(), user);
                return user;
            } else {
                throw new ValidationException("id обновляемого пользователя отсутствует");
            }
        } else {
            throw new ValidationException("Данные пользователя не соответствуют критериям");
        }
    }
}