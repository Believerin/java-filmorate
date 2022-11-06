package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserServing {

    List<User> addFriend(Integer userId, Integer friendId);

    List<User> deleteFriend(Integer userId, Integer friendId);

    Set<User> getAllFriends(Integer userId);

    List<User> confirmFriend(Integer userId, Integer friendId);

    Set<User> getCommonFriends(Integer userId, Integer otherUserId);
}
