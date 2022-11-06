package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Qualifier("Secondary")
public class UserService implements UserServing {

    private final UserStorage userStorage;
    private final UserServing userServing;

    @Autowired
    public UserService(@Qualifier("priority") UserStorage userStorage, @Qualifier("priority") UserServing userServing) {
        this.userStorage = userStorage;
        this.userServing = userServing;
    }

    public List<User> addFriend(Integer userId, Integer friendId) {
        return userServing.addFriend(userId, friendId);
    }

    public List<User> deleteFriend(Integer userId, Integer friendId) {
        return userServing.deleteFriend(userId, friendId);
    }

    public List<User> confirmFriend(Integer userId, Integer friendId) {
        return userServing.confirmFriend(userId, friendId);
    }

    public Set<User> getAllFriends(Integer userId) {
        return userServing.getAllFriends(userId);
    }

    public Set<User> getCommonFriends(Integer userId, Integer otherUserId) {
        return userServing.getCommonFriends(userId, otherUserId);
    }

    //............................ Служебные методы ..............................................
    private List<User> filterCoupleOfUsersWhoBecomeFriends (Integer userId, Integer friendId) {
        return userStorage.getUsers().values().stream()
                .filter(user -> user.getId().equals(userId) || user.getId().equals(friendId))
                .collect(Collectors.toList());
    }
}
