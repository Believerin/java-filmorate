package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserStorage {

    Collection<User> findAll();

    User getUser(@PathVariable Integer id);

    User createUser(User user);

    User updateUser(User user);

    Map<Integer, User> getUsers();
}
