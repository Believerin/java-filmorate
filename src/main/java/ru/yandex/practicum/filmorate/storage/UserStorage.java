package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NoSuchBodyException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.sql.*;
import java.util.*;

@Repository
public class UserStorage{

    private final JdbcTemplate jdbcTemplate;

    public UserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public Collection<User> findAll() {
        String sql = "SELECT * " +
                "FROM FILM_USER;";
        return jdbcTemplate.query(sql, UserStorage::mapRowToUser);
    }

    public User getUserById(Integer id) {
        String sql = "SELECT * " +
                "FROM FILM_USER " +
                "WHERE USER_ID = ?;";
        try {
            return jdbcTemplate.queryForObject(sql, UserStorage::mapRowToUser, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public User createUser(User user) {
        String sql = "INSERT INTO FILM_USER (EMAIL, LOGIN, NAME, BIRTHDAY) " +
                "VALUES (?, ?, ?, ?);";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName()
                , user.getBirthday());
        return findAll().stream()
                .filter(o -> Objects.equals(o.getEmail(), user.getEmail()))
                .findFirst()
                .orElse(null);
    }

    public User updateUser(User user) {
        String sql = "UPDATE FILM_USER " +
                "SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? " +
                "WHERE USER_ID = ?;";
        boolean doesExist = jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName()
                , user.getBirthday(), user.getId()) > 0;
        return doesExist ? user : null;
    }

    public List<User> addFriend(Integer userId, Integer friendId) {
        if (getUserById(friendId) == null) throw new NoSuchBodyException("friendId друга отсутствует");
        String sql = "INSERT INTO FRIENDSHIP (USER_ID, FRIEND_ID, STATUS) " +
                "VALUES (?, ?, 'NOT_CONFIRMED');";
        jdbcTemplate.update(sql, userId, friendId);
        return List.of(getUserById(userId), getUserById(friendId));
    }

    public List<User> deleteFriend(Integer userId, Integer friendId) {
        String sql = "DELETE " +
                "FROM FRIENDSHIP " +
                "WHERE USER_ID = ? AND FRIEND_ID = ?;";
        jdbcTemplate.update(sql, userId, friendId);
        return List.of(getUserById(userId), getUserById(friendId));
    }

    public List<User> confirmFriend(Integer userId, Integer friendId) {
        String sql = "UPDATE FRIENDSHIP " +
                "SET STATUS = 'CONFIRMED' " +
                "WHERE USER_ID =? AND FRIEND_ID = ?;";
        return jdbcTemplate.query(sql, UserStorage::mapRowToUser, userId);
    }

    public Set<User> getAllFriends(Integer userId) {
        if (getUserById(userId) == null) throw new NoSuchBodyException("userId пользователя отсутствует");
        String sql = "SELECT USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY " +
                "FROM " +
                "(SELECT FRIEND_ID " +
                "FROM FRIENDSHIP " +
                "WHERE USER_ID = ?) AS fr " +
                "LEFT JOIN FILM_USER AS f ON f.USER_ID = fr.FRIEND_ID;";
        return new HashSet<>(jdbcTemplate.query(sql, UserStorage::mapRowToUser, userId));
    }

    public Set<User> getCommonFriends(Integer userId, Integer otherUserId) {
        String sql = "SELECT USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY " +
                "FROM FILM_USER " +
                "WHERE USER_ID IN ( " +
                "SELECT FRIEND_ID " +
                "FROM FRIENDSHIP " +
                "WHERE USER_ID = ? AND FRIEND_ID IN (SELECT FRIEND_ID FROM FRIENDSHIP WHERE USER_ID = ?));";
        return new HashSet<>(jdbcTemplate.query(sql, UserStorage::mapRowToUser, userId, otherUserId));
    }

    public User delete(Integer userId) {
        User user = getUserById(userId);
        String sqlQuery = "DELETE FROM FILM_USER WHERE USER_ID=?";
        jdbcTemplate.update(sqlQuery, userId);
        return user;
    }

    //............................ Служебные методы ..............................................

    private static User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("user_id"))
                .email((resultSet.getString("email")))
                .login((resultSet.getString("login")))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }
}