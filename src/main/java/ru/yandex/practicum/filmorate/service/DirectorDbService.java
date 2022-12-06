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
    private final FilmService filmService;

    @Override
    public Collection<Director> findAllDirectors() {
        String sql = "SELECT * FROM DIRECTORS";
        try {
            return jdbcTemplate.query(sql, Director::mapRowToDirector);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public Director getDirectorById(int directorId) {
        String sql = "SELECT * FROM DIRECTORS WHERE DIRECTOR_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Director::mapRowToDirector, directorId);
        } catch (Exception e) {
            return null;
        }
    }
    @Override
    public List<Director> getDirectorsOfFilm(int filmId) {
        String sqlFromDirectorsFilm = "SELECT dirs.* FROM DIRECTORS AS dirs " +
                "JOIN DIRECTORS_FILM AS df ON df.DIRECTOR_ID = dirs.DIRECTOR_ID " +
                "WHERE df.FILM_ID = ?";
        try {
            return jdbcTemplate.query(sqlFromDirectorsFilm, Director::mapRowToDirector, filmId);
        } catch (Exception e) {
            return List.of();
        }
    }

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

    @Override
    public Director updateDirector(Director director) {
        String sql = "UPDATE DIRECTORS SET DIRECTOR_NAME = ? WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return director;
    }

    @Override
    public void removeDirector(int directorId) {
        String sql = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(sql, directorId);
    }


    public boolean checkAndConnectDirectorAndFilm(Film film) {
            int directorId = film.getDirectors().get(0).getId();
            Director director = getDirectorById(directorId);
            if (director != null) {
                return connectDirectorAndFilm(film.getId(), directorId);
            }
        return false;
    }
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

    @Override
    public Collection<Film> getFilmsByDirectorSortByLikes(int directorId) {
        String sql = "SELECT * FROM FILM AS f " +
                "JOIN LIKES AS l ON l.FILM_ID=f.FILM_ID " +
                "WHERE DIRECTOR_ID = ? " +
                "ORDER BY COUNT(l.USER_ID) DESC";
        return jdbcTemplate.query(sql, FilmDbService::mapRowToFilmGetter, directorId);
    }

    @Override
    public Collection<Film> getFilmsByDirectorSortByReleaseYear(int directorId) {
        return filmService.findAll().stream()
                .filter(film -> film.getDirectors().get(0).getId() == directorId)
                .sorted(Comparator.comparingInt(f -> f.getReleaseDate().getYear()))
                .collect(Collectors.toList());
    }

}