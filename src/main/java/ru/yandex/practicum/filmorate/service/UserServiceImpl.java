package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoSuchBodyException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Qualifier("Secondary")
public class UserServiceImpl implements UserService {

    private final UserDbService userDbService;
    private final EventServiceImpl eventService;

    @Autowired
    public UserServiceImpl(UserDbService userDbService, EventServiceImpl eventService) {
        this.userDbService = userDbService;
        this.eventService = eventService;
    }

    public Collection<User> findAll() {
        return userDbService.findAll();
    }

    public User getUserById(Integer id) {
        User user = userDbService.getUserById(id);
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
        User o = userDbService.createUser(user);
        if (o == null) {
            throw new ValidationException("Пользователь уже существует");
        }
        return o;
    }

    public User updateUser(User user) {
        if (userDbService.updateUser(user) == null) {
            throw new NoSuchBodyException(String.format("Пользователь с id %s отсутствует", user.getId()));
        }
        return user;
    }

    public List<User> addFriend(Integer userId, Integer friendId) {
        final Event event = eventService.saveEvent("FRIEND", "ADD", userId, friendId);
        eventService.createEvent(event);
        return userDbService.addFriend(userId, friendId);
    }

    public List<User> deleteFriend(Integer userId, Integer friendId) {
        final Event event = eventService.saveEvent("FRIEND", "REMOVE", userId, friendId);
        eventService.createEvent(event);
        return userDbService.deleteFriend(userId, friendId);
    }

    public List<User> confirmFriend(Integer userId, Integer friendId) {
        return userDbService.confirmFriend(userId, friendId);
    }

    public Set<User> getAllFriends(Integer userId) {
        return userDbService.getAllFriends(userId);
    }

    public Set<User> getCommonFriends(Integer userId, Integer otherUserId) {
        return userDbService.getCommonFriends(userId, otherUserId);
    }

    /*Эндпоинт для удаления пользователей*/
    @Override
    public User delete(Integer userId) {
        User user = userDbService.delete(userId);
        if (user == null) {
            throw new NoSuchBodyException(String.format("Пользователь с id %s отсутствует", userId));
        } else {
            return user;
        }
    }
    /*Эндпоинт для удаления пользователей*/
}
