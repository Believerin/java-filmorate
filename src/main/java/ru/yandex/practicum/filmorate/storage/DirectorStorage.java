package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Repository
public class DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    public Collection<Director> findAllDirectors() {
        String sql = "SELECT * FROM DIRECTORS";
        try {
            return jdbcTemplate.query(sql, DirectorStorage::mapRowToDirector);
        } catch (Exception e) {
            return List.of();
        }
    }

    public Director getDirectorById(int directorId) {
        String sql = "SELECT * FROM DIRECTORS WHERE DIRECTOR_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, DirectorStorage::mapRowToDirector, directorId);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Director> getDirectorsOfFilm(int filmId) {
        String sqlFromDirectorsFilm = "SELECT dirs.* FROM DIRECTORS AS dirs " +
                "JOIN DIRECTORS_FILM AS df ON df.DIRECTOR_ID = dirs.DIRECTOR_ID " +
                "WHERE df.FILM_ID = ?";
        try {
            return jdbcTemplate.query(sqlFromDirectorsFilm, DirectorStorage::mapRowToDirector, filmId);
        } catch (Exception e) {
            return List.of();
        }
    }

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

    public Director updateDirector(Director director) {
        String sql = "UPDATE DIRECTORS SET DIRECTOR_NAME = ? WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return director;
    }


    public void removeDirector(int directorId) {
        String sql = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(sql, directorId);
    }

    public boolean connectFilmAndDirector(int filmId, int directorId) {
        String sql = "INSERT INTO DIRECTORS_FILM (DIRECTOR_ID, FILM_ID) VALUES (?,?)";
        try {
            jdbcTemplate.update(sql, directorId, filmId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean disconnectFilmAndDirector(int filmId, int directorId) {
        String sql = "DELETE FROM DIRECTORS_FILM WHERE FILM_ID = ? AND DIRECTOR_ID = ?";
        try {
            jdbcTemplate.update(sql, directorId, filmId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Integer> getFilmsByDirectorSortByLikes() {
        String sql = "SELECT FILM_ID FROM LIKES " +
                "GROUP BY FILM_ID " +
                "ORDER BY COUNT(USER_ID) DESC";
        return jdbcTemplate.queryForList(sql, Integer.TYPE);
    }

    //__________________________________СЛУЖЕБНЫЕ МЕТОДЫ_________________________________________

    /**
     * Метод, аналогичный методу в FilmService.
     * Введен для того, чтобы избежать цикличной зависимости между DirectorService и FilmService.
     */
    public Collection<Film> getAllFilmsByDirector(int directorId) {
        String sqlFromGenreFilm = "SELECT gf.GENRE_ID, g.GENRE_NAME " +
                "FROM GENRE_FILM AS gf " +
                "LEFT JOIN GENRE AS g ON g.GENRE_ID = gf.GENRE_ID " +
                "WHERE FILM_ID = ?;";
        String sql = "SELECT * " +
                "FROM FILM AS f " +
                "JOIN MPA AS m ON f.MPA_ID = m.MPA_ID;";
        return jdbcTemplate.query(sql, FilmStorage::mapRowToFilmGetter).stream()
                .peek(film -> film.setGenres(jdbcTemplate.query(
                        sqlFromGenreFilm, FilmStorage::mapRowToGenreFilmGetter, film.getId())))
                .peek(film -> film.setDirectors(getDirectorsOfFilm(film.getId())))
                .filter(film -> film.getDirectors().equals(getDirectorsOfFilm(directorId)))
                .collect(Collectors.toList());
    }

    /**
     * Метод для отображения строк. Передается в методы JdbcTemplate
     */
    public static Director mapRowToDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getInt("DIRECTOR_ID"))
                .name(rs.getString("DIRECTOR_NAME"))
                .build();
    }
}