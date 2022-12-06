package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.status.Genres;
import ru.yandex.practicum.filmorate.status.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Qualifier("priority")
public class FilmDbService implements FilmService {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> findAll() {
        String sqlFromGenreFilm = "SELECT gf.GENRE_ID, g.GENRE_NAME " +
                "FROM GENRE_FILM AS gf " +
                "LEFT JOIN GENRE AS g ON g.GENRE_ID = gf.GENRE_ID " +
                "WHERE FILM_ID = ?;";
        String sql = "SELECT * " +
                "FROM FILM AS f " +
                "JOIN MPA AS m ON f.MPA_ID = m.MPA_ID;";
        return jdbcTemplate.query(sql, FilmDbService::mapRowToFilm).stream()
                .peek(film -> film.setGenres(jdbcTemplate.query(
                        sqlFromGenreFilm, FilmDbService::mapRowToGenreFilm, film.getId()))
                ).collect(Collectors.toList());
    }

    @Override
    public Film getFilmById(Integer id) {
        String sqlFromGenreFilm = "SELECT gf.GENRE_ID, g.GENRE_NAME " +
                "FROM GENRE_FILM AS gf " +
                "LEFT JOIN GENRE AS g ON g.GENRE_ID = gf.GENRE_ID " +
                "WHERE FILM_ID = ?;";
        List<Map<String, Object>> genres =
                jdbcTemplate.query(sqlFromGenreFilm, FilmDbService::mapRowToGenreFilm, id);
        String sql = "SELECT * " +
                "FROM FILM AS f " +
                "JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                "WHERE FILM_ID = ?;";
        try {
            Film film = jdbcTemplate.queryForObject(sql, FilmDbService::mapRowToFilm, id);
            if (film != null) film.setGenres(genres);
            return film;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Film createFilm(Film film) {
        fillMpaTable();
        fillGenreTable();
        int film_id = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("film_id")
                .executeAndReturnKey(toMap(film))
                .intValue();

        if (film.getGenres() != null) {

            StringBuilder sqlGenreFilm =  new StringBuilder("INSERT INTO GENRE_FILM (FILM_ID, GENRE_ID) ");
            film.getGenres().stream()
                    .map(map -> map.get("id"))
                    .forEach(genreId -> sqlGenreFilm.append(String.format("VALUES (%d, %d) ", film_id, (int)genreId)));
            sqlGenreFilm.append(";");
            jdbcTemplate.update(String.valueOf(sqlGenreFilm));
        }
        return getFilmById(film_id);
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE FILM " +
                "SET FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? " +
                "WHERE FILM_ID = ?;";
        boolean doesExist = jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate()
                , film.getDuration(), film.getMpa().get("id"), film.getId()) > 0;
        jdbcTemplate.update("DELETE FROM GENRE_FILM WHERE FILM_ID = ?;", film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            StringBuilder sqlGenreFilm =  new StringBuilder("INSERT INTO GENRE_FILM (FILM_ID, GENRE_ID) VALUES ");
            film.getGenres().stream()
                    .distinct()
                    .map(map -> map.get("id"))
                    .forEach(genreId -> sqlGenreFilm.append(String.format("(%s, %s), ", film.getId(), genreId)));
            sqlGenreFilm.delete(sqlGenreFilm.length() - 2, sqlGenreFilm.length()).append(";");
            jdbcTemplate.update(String.valueOf(sqlGenreFilm));
        }
        return doesExist ? getFilmById(film.getId()) : null;
    }

    public Mpa getMpa (int id) {
        String sql = "SELECT * " +
                "FROM MPA " +
                "WHERE MPA_ID = ?;";
        return jdbcTemplate.queryForObject(sql, FilmDbService::mapRowToMpa, id);
    }

    public Collection<Mpa> findAllMpa() {
        String sql = "SELECT * " +
                "FROM MPA;";
        return jdbcTemplate.query(sql, FilmDbService::mapRowToMpa);
    }

    public Genre getGenre (int id) {
        String sql = "SELECT * " +
                "FROM GENRE " +
                "WHERE GENRE_ID = ?;";
        return jdbcTemplate.queryForObject(sql, FilmDbService::mapRowToGenre, id);
    }

    @Override
    public Collection<Genre> findAllGenres() {
        String sql = "SELECT * " +
                "FROM GENRE;";
        return jdbcTemplate.query(sql, FilmDbService::mapRowToGenre);
    }


    public Film addLike (Integer filmId, Integer userId) {
        String sql = "INSERT INTO LIKES (FILM_ID, USER_iD) " +
                     "VALUES (?, ?);";
        jdbcTemplate.update(sql, filmId, userId);
        return getFilmById(filmId);
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
        return getFilmById(filmId);
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
        return jdbcTemplate.query(sql, FilmDbService::mapRowToFilm, count).stream()
                .peek(film -> film.setGenres(jdbcTemplate.query(
                        sqlFromGenreFilm, FilmDbService::mapRowToGenreFilm, film.getId())
                        )).collect(Collectors.toList());
    }
    @Override
    public List<Film> getCommonFilms(int userId, int friendId){
        String sqlQuery = "SELECT f.*, m.* FROM FILM f " +
                "JOIN MPA m on f.MPA_ID = m.MPA_ID " +
                "JOIN LIKES l on f.FILM_ID = l.FILM_ID " +
                "WHERE f.FILM_ID IN" +
                "(SELECT DISTINCT l.FILM_ID " +
                "FROM LIKES l " +
                "WHERE l.USER_ID = ? " +
                "AND l.FILM_ID IN ( " +
                "SELECT l.FILM_ID " +
                "FROM LIKES l " +
                "WHERE l.USER_ID =?)) " +
                "GROUP BY f.FILM_ID " +
                "ORDER BY COUNT(l.USER_ID) DESC;";
        return jdbcTemplate.query(sqlQuery, FilmDbService::mapRowToFilm, userId, friendId);
    }

    //............................ Служебные методы ..............................................

    private static Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Map<String, Object> m = new HashMap<>();
        m.put("id", resultSet.getInt("mpa_id"));
        m.put("name", resultSet.getString("mpa_name"));
        return Film.builder()
                .id(resultSet.getInt("film_id"))
                .name(resultSet.getString("film_name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(m)
                .build();
    }

    private static Map<String, Object> mapRowToGenreFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Map<String, Object> o = new HashMap<>();
        o.put("id", resultSet.getInt("genre_id"));
        o.put("name", resultSet.getString("genre_name"));
        return o;
    }

    private static Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("mpa_id"))
                .name(resultSet.getString("mpa_name"))
                .build();
    }

    private static Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(Genres.valueOf(resultSet.getString("genre_name")))
                .build();
    }

    private void fillMpaTable() {
        if (jdbcTemplate.queryForList("SELECT MPA_ID FROM MPA;", Integer.class).isEmpty()) {
            String sql = "INSERT INTO MPA (MPA_ID, MPA_NAME) " +
                         "VALUES (?, ?);";
            for (Rating rating : Rating.values()) {
                Mpa o = new Mpa(rating);
                jdbcTemplate.update(sql, o.getId(), o.getName());
            }
        }
    }
    private void fillGenreTable() {
        if (jdbcTemplate.queryForList("SELECT GENRE_ID FROM GENRE;", Integer.class).isEmpty()) {
            String sql = "INSERT INTO GENRE (GENRE_ID, GENRE_NAME) " +
                         "VALUES (?, ?);";
            for (Genres genres : Genres.values()) {
                Genre o = new Genre(genres);
                jdbcTemplate.update(sql, o.getId(), o.getName().toString());
            }
        }
    }

    private Map<String, Object> toMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("film_name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        if (film.getMpa() != null) {
            values.put("mpa_id", film.getMpa().get("id"));
        }
        return values;
    }
}