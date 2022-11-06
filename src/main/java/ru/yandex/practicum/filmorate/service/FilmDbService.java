package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Qualifier("priority")
public class FilmDbService implements FilmServing {

    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;

    public FilmDbService(JdbcTemplate jdbcTemplate, @Qualifier("priority") FilmStorage filmStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmStorage = filmStorage;
    }

    public Film addLike (Integer filmId, Integer userId) {
        String sql = "INSERT INTO LIKES (FILM_ID, USER_iD) " +
                     "VALUES (?, ?);";
        jdbcTemplate.update(sql, filmId, userId);
        return filmStorage.getFilmById(filmId);
    }

    public List<Integer> getUsersLikesByFilmId(Integer filmId) {
        String sql = "SELECT USER_ID FROM LIKES WHERE FILM_ID = ?;";
        List<Integer> usersId = jdbcTemplate.queryForList(sql, Integer.class, filmId);
        return !usersId.isEmpty() ? usersId : null;
    }

    @Override
    public Film deleteLike(Integer filmId, Integer userId) {
        String sql = "DELETE " +
                     "FROM LIKES " +
                     "WHERE FILM_ID = ? AND USER_ID = ?;";
        jdbcTemplate.update(sql, filmId, userId);
        return filmStorage.getFilmById(filmId);
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        String sqlFromGenreFilm = "SELECT gf.GENRE_ID, g.GENRE_NAME " +
                                  "FROM GENRE_FILM AS gf " +
                                  "LEFT JOIN GENRE AS g ON g.GENRE_ID = gf.GENRE_ID " +
                                  "WHERE FILM_ID = ?;";
        String sql = "SELECT * " +
                     "FROM FILM AS f " +
                     "LEFT JOIN " +
                     "(SELECT * " +
                     "FROM LIKES) AS l ON l.FILM_ID = f.FILM_ID " +
                     "JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                     "GROUP BY f.FILM_ID " +
                     "ORDER BY COUNT(USER_ID) DESC " +
                     "LIMIT ?;";
        return jdbcTemplate.query(sql, FilmDbStorage::mapRowToFilm, count).stream()
                .peek(film -> film.setGenres(jdbcTemplate.query(
                        sqlFromGenreFilm, FilmDbStorage::mapRowToGenreFilm, film.getId())
                        )).collect(Collectors.toList());
    }
}