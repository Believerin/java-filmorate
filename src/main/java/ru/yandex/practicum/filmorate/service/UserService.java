package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements UserServing {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> addFriend(Integer userId, Integer friendId) {
        Map<Integer, User> o =  userStorage.getUsers();
        filterUsersWithFriendship(userId, friendId)
            .forEach(user -> {
                if (user.equals(o.get(userId))) {
                    user.getFriends().add(o.get(friendId).getId());
                } else {
                    user.getFriends().add(o.get(userId).getId());
                }
            });
        return filterUsersWithFriendship(userId, friendId);
    }

    public List<User> deleteFriend(Integer userId, Integer friendId) {
        filterUsersWithFriendship(userId, friendId)
                .forEach(user -> {
                     user.getFriends().removeIf(friend -> filterUsersWithFriendship(userId, friendId).stream()
                             .map(User::getId)
                             .collect(Collectors.toList()).contains(friend));
                });
        return filterUsersWithFriendship(userId, friendId);
    }

    public Set<User> getAllFriends(Integer userId) {
        return userStorage.getUsers().get(userId).getFriends().stream()
                .map(o -> userStorage.getUsers().get(o))
                .collect(Collectors.toSet());
    }

    public Set<User> getCommonFriends(Integer userId, Integer otherUserId) {
        return getAllFriends(userId).stream()
               .filter(friend -> getAllFriends(otherUserId).contains(friend))
               .collect(Collectors.toSet());
    }

    //............................ Служебные методы ..............................................
    private List<User> filterUsersWithFriendship (Integer userId, Integer friendId) {
        return userStorage.getUsers().values().stream()
                .filter(user -> user.getId().equals(userId) || user.getId().equals(friendId))
                .collect(Collectors.toList());
    }
}
