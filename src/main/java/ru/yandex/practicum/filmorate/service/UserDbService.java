package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Qualifier("priority")
public class UserDbService implements UserServing {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    public UserDbService(JdbcTemplate jdbcTemplate, @Qualifier("priority") UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }


    @Override
    public List<User> addFriend(Integer userId, Integer friendId) {
        String sql = "INSERT INTO FRIENDSHIP (USER_ID, FRIEND_ID, STATUS) " +
                     "VALUES (?, ?, 'NOT_CONFIRMED');";
        jdbcTemplate.update(sql, userId, friendId);
        return List.of(userStorage.getUserById(userId), userStorage.getUserById(friendId));
    }

    @Override
    public List<User> deleteFriend(Integer userId, Integer friendId) {
        String sql = "DELETE " +
                     "FROM FRIENDSHIP " +
                     "WHERE USER_ID = ? AND FRIEND_ID = ?;";
        jdbcTemplate.update(sql, userId, friendId);
        return List.of(userStorage.getUserById(userId), userStorage.getUserById(friendId));
    }

    @Override
    public List<User> confirmFriend(Integer userId, Integer friendId) {
        String sql = "UPDATE FRIENDSHIP " +
                     "SET STATUS = 'CONFIRMED' " +
                     "WHERE USER_ID =? AND FRIEND_ID = ?;";
        return jdbcTemplate.query(sql, UserDbStorage::mapRowToUser, userId);
    }

    @Override
    public Set<User> getAllFriends(Integer userId) {
        String sql = "SELECT USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY " +
                     "FROM " +
                     "(SELECT FRIEND_ID " +
                     "FROM FRIENDSHIP " +
                     "WHERE USER_ID = ?) AS fr " +
                     "LEFT JOIN FILM_USER AS f ON f.USER_ID = fr.FRIEND_ID;";
        return new HashSet<>(jdbcTemplate.query(sql, UserDbStorage::mapRowToUser, userId));
    }

    @Override
    public Set<User> getCommonFriends(Integer userId, Integer otherUserId) {
        String sql = "SELECT USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY " +
                     "FROM FILM_USER " +
                     "WHERE USER_ID IN ( " +
                     "SELECT FRIEND_ID " +
                     "FROM FRIENDSHIP " +
                     "WHERE USER_ID = ? AND FRIEND_ID IN (SELECT FRIEND_ID FROM FRIENDSHIP WHERE USER_ID = ?));";
        return new HashSet<>(jdbcTemplate.query(sql, UserDbStorage::mapRowToUser, userId, otherUserId));
    }
}