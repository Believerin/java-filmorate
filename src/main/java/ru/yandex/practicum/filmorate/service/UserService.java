package ru.yandex.practicum.filmorate.service;

import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface UserService {

    Collection<User> findAll();

    User getUserById(@PathVariable Integer id);

    User createUser(User user);

    User updateUser(User user);

    List<User> addFriend(Integer userId, Integer friendId);

    List<User> deleteFriend(Integer userId, Integer friendId);

    Set<User> getAllFriends(Integer userId);

    List<User> confirmFriend(Integer userId, Integer friendId);

    Set<User> getCommonFriends(Integer userId, Integer otherUserId);
}
