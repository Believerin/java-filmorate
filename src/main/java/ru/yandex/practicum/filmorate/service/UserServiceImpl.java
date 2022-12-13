package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoSuchBodyException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final EventServiceImpl eventService;

    @Autowired
    public UserServiceImpl(UserStorage userStorage, EventServiceImpl eventService) {
        this.userStorage = userStorage;
        this.eventService = eventService;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User getUserById(Integer id) {
        User user = userStorage.getUserById(id);
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
        User o = userStorage.createUser(user);
        if (o == null) {
            throw new ValidationException("Пользователь уже существует");
        }
        return o;
    }

    public User updateUser(User user) {
        if (userStorage.updateUser(user) == null) {
            throw new NoSuchBodyException(String.format("Пользователь с id %s отсутствует", user.getId()));
        }
        return user;
    }

    public List<User> addFriend(Integer userId, Integer friendId) {
        final Event event = eventService.saveEvent("FRIEND", "ADD", userId, friendId);
        eventService.createEvent(event);
        return userStorage.addFriend(userId, friendId);
    }

    public List<User> deleteFriend(Integer userId, Integer friendId) {
        final Event event = eventService.saveEvent("FRIEND", "REMOVE", userId, friendId);
        eventService.createEvent(event);
        return userStorage.deleteFriend(userId, friendId);
    }

    public List<User> confirmFriend(Integer userId, Integer friendId) {
        return userStorage.confirmFriend(userId, friendId);
    }

    public Set<User> getAllFriends(Integer userId) {
        return userStorage.getAllFriends(userId);
    }

    public Set<User> getCommonFriends(Integer userId, Integer otherUserId) {
        return userStorage.getCommonFriends(userId, otherUserId);
    }

    /*Эндпоинт для удаления пользователей*/
    @Override
    public User delete(Integer userId) {
        User user = userStorage.delete(userId);
        if (user == null) {
            throw new NoSuchBodyException(String.format("Пользователь с id %s отсутствует", userId));
        } else {
            return user;
        }
    }
    /*Эндпоинт для удаления пользователей*/
}
