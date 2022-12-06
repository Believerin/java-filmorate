package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectorDbService implements DirectorService {

    private final JdbcTemplate jdbcTemplate;

    /**Вывести список всех режиссеров*/
    @Override
    public Collection<Director> findAllDirectors() {
        String sql = "SELECT * FROM DIRECTORS";
        try {
            return jdbcTemplate.query(sql, Director::mapRowToDirector);
        } catch (Exception e) {
            return List.of();
        }
    }

    /**Найти режиссера по DIRECTOR_ID*/
    @Override
    public Director getDirectorById(int directorId) {
        String sql = "SELECT * FROM DIRECTORS WHERE DIRECTOR_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Director::mapRowToDirector, directorId);
        } catch (Exception e) {
            return null;
        }
    }

    /**Добавить нового режиссера*/
    @Override
    public Director addNewDirector(Director director) {
        String sql = "INSERT INTO DIRECTORS(DIRECTOR_NAME) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"DIRECTOR_ID"});
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);
        director.setId(keyHolder.getKey().intValue());
        return director;
    }

    /**Обновить данные режиссера*/
    @Override
    public Director updateDirector(Director director) {
        String sql = "UPDATE DIRECTORS SET DIRECTOR_NAME = ? WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return director;
    }

    /**Удалить режиссера по DIRECTOR_ID*/
    @Override
    public void removeDirector(int directorId) {
        String sql = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(sql, directorId);
    }

    /**Связать режиссера и фильм в таблице DIRECTORS_FILM*/
    @Override
    public boolean connectDirectorAndFilm(int filmId, int directorId) {
        String sql = "INSERT INTO DIRECTORS_FILM (DIRECTOR_ID, FILM_ID) VALUES (?,?)";
        try {
            jdbcTemplate.update(sql, directorId, filmId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**Удалить пару режиссер/фильм из таблицы DIRECTORS_FILM*/
    @Override
    public boolean disconnectDirectorAndFilm(int filmId, int directorId) {
        String sql = "DELETE FROM DIRECTORS_FILM WHERE FILM_ID = ? AND DIRECTOR_ID = ?";
        try {
            jdbcTemplate.update(sql, directorId, filmId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

/*
    //............................ Служебные методы ..............................................

    public static Director mapRowToDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getInt("DIRECTOR_ID"))
                .name(rs.getString("DIRECTOR_NAME"))
                .build();
    }*/

}