package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoSuchBodyException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {

    @Getter
    private final Map<Integer, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public User getUser(Integer id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NoSuchBodyException(String.format("Пользователь с id %s отсутствует", id));
        }
    }

    public User createUser(User user) {
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
    }

    public User updateUser(User user) {
        if (user.getName()==null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        } else {
            throw new NoSuchBodyException(String.format("Пользователь с id %s отсутствует", user.getId()));
        }
    }

}
