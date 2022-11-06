package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoSuchBodyException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Qualifier("Secondary")
public class InMemoryUserStorage implements UserStorage {

    private final UserDbStorage userDbStorage;

    @Getter
    private final Map<Integer, User> users = new HashMap<>();

    public InMemoryUserStorage(UserDbStorage userDbStorage) {
        this.userDbStorage = userDbStorage;
    }

    public Collection<User> findAll() {
        return userDbStorage.findAll();
    }

    public User getUserById(Integer id) {
        User user = userDbStorage.getUserById(id);
        if (user != null) {
            return user;
        } else {
            throw new NoSuchBodyException(String.format("Пользователь с id %s отсутствует", id));
        }
    }

    public User createUser(User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        User o = userDbStorage.createUser(user);
        if (o == null) {
            throw new ValidationException("Пользователь уже существует");
        }
        return o;
    }

    public User updateUser(User user) {
        if (userDbStorage.updateUser(user) == null) {
            throw new NoSuchBodyException(String.format("Пользователь с id %s отсутствует", user.getId()));
        }
        return user;
    }
}
