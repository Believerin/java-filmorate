package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectorDbService implements DirectorService {

    private final JdbcTemplate jdbcTemplate;
    private final FilmDbService filmDbService;

    /**Вывести список всех режиссеров*/
    public Collection<Director> findAllDirectors() {
        String sql = "SELECT * FROM DIRECTORS";
        return jdbcTemplate.query(sql, DirectorDbService::mapRowToDirector);
    }

    /**Найти режиссера по DIRECTOR_ID*/
    public Director getDirectorById(int directorId) {
        String sql = "SELECT * FROM DIRECTORS WHERE DIRECTOR_ID = ?";
        return jdbcTemplate.queryForObject(sql, DirectorDbService::mapRowToDirector, directorId);
    }

    /**Добавить нового режиссера*/
    public void addNewDirector(Director director) {
        String sql = "INSERT INTO DIRECTORS(DIRECTOR_ID) VALUES (?)";
        jdbcTemplate.update(sql, director.getName());
    }

    /**Обновить данные режиссера*/
    public void updateDirector(Director director) {
        String sql = "UPDATE DIRECTORS SET DIRECTOR_NAME = ? WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
    }

    /**Удалить режиссера по DIRECTOR_ID*/
    public void removeDirector(int directorId) {
        String sql = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(sql, directorId);
    }



    //............................ Служебные методы ..............................................

    private static Director mapRowToDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getInt("DIRECTOR_ID"))
                .name(rs.getString("DIRECTOR_NAME"))
                .build();
    }

}