package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@Component
@Qualifier("priority")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> findAll() {
        String sql = "SELECT * " +
                "FROM FILM_USER;";
        return jdbcTemplate.query(sql, UserDbStorage::mapRowToUser);
    }

    @Override
    public User getUserById(Integer id) {
        String sql = "SELECT * " +
                     "FROM FILM_USER " +
                     "WHERE USER_ID = ?;";
        try {
            return jdbcTemplate.queryForObject(sql, UserDbStorage::mapRowToUser, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
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

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE FILM_USER " +
                     "SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? " +
                     "WHERE USER_ID = ?;";
        boolean doesExist = jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName()
                , user.getBirthday(), user.getId()) > 0;
        return doesExist ? user : null;
    }

    @Override
    public Map<Integer, User> getUsers() { // Геттер от старого кода
        return null;
    }

    //............................ Служебные методы ..............................................

    public static User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("user_id"))
                .email((resultSet.getString("email")))
                .login((resultSet.getString("login")))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }
}