package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.status.Genres;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("priority")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
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
        return jdbcTemplate.query(sql, FilmDbStorage::mapRowToFilm).stream()
                .peek(film -> film.setGenres(jdbcTemplate.query(
                        sqlFromGenreFilm, FilmDbStorage::mapRowToGenreFilm, film.getId()))
                        ).collect(Collectors.toList());
    }

    @Override
    public Film getFilmById(Integer id) {
        String sqlFromGenreFilm = "SELECT gf.GENRE_ID, g.GENRE_NAME " +
                                  "FROM GENRE_FILM AS gf " +
                                  "LEFT JOIN GENRE AS g ON g.GENRE_ID = gf.GENRE_ID " +
                                  "WHERE FILM_ID = ?;";
        List<Map<String, Object>> genres =
                jdbcTemplate.query(sqlFromGenreFilm, FilmDbStorage::mapRowToGenreFilm, id);
        String sql = "SELECT * " +
                     "FROM FILM AS f " +
                     "JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                     "WHERE FILM_ID = ?;";
        try {
            Film film = jdbcTemplate.queryForObject(sql, FilmDbStorage::mapRowToFilm, id);
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
        Collection<Film> f = findAll();

        if (film.getGenres() != null) {
            String sqlGenreFilm = "INSERT INTO GENRE_FILM (FILM_ID, GENRE_ID) " +
                                  "VALUES (?, ?);";
            film.getGenres().stream()
                    .map(map -> map.get("id"))
                    .forEach(id -> jdbcTemplate.update(sqlGenreFilm, film_id, id));
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
        if (film.getGenres() != null) {
            String sqlGenreFilm = "INSERT INTO GENRE_FILM (FILM_ID, GENRE_ID) " +
                                  "VALUES (?, ?);";
            List<Object> t = film.getGenres().stream()
                    .map(map -> map.get("id"))
                    .distinct().collect(Collectors.toList());
            film.getGenres().stream()
                    .map(map -> map.get("id"))
                    .distinct()
                    .forEach(id -> jdbcTemplate.update(sqlGenreFilm, film.getId(), id));
        }
        return doesExist ? getFilmById(film.getId()) : null;
    }

    public Mpa getMpa (int id) {
        String sql = "SELECT * " +
                     "FROM MPA " +
                     "WHERE MPA_ID = ?;";
        return jdbcTemplate.queryForObject(sql, FilmDbStorage::mapRowToMpa, id);
    }

    public Collection<Mpa> findAllMpa() {
        String sql = "SELECT * " +
                     "FROM MPA;";
        return jdbcTemplate.query(sql, FilmDbStorage::mapRowToMpa);
    }

    public Genre getGenre (int id) {
        String sql = "SELECT * " +
                     "FROM GENRE " +
                     "WHERE GENRE_ID = ?;";
        return jdbcTemplate.queryForObject(sql, FilmDbStorage::mapRowToGenre, id);
    }

    @Override
    public Collection<Genre> findAllGenres() {
        String sql = "SELECT * " +
                     "FROM GENRE;";
        return jdbcTemplate.query(sql, FilmDbStorage::mapRowToGenre);
    }

    //............................ Служебные методы ..............................................

    public static Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
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

    public static Map<String, Object> mapRowToGenreFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Map<String, Object> o = new HashMap<>();
        o.put("id", resultSet.getInt("genre_id"));
        o.put("name", resultSet.getString("genre_name"));
        return o;
    }

    public static Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("mpa_id"))
                .name(resultSet.getString("mpa_name"))
                .build();
    }

    public static Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(Genres.valueOf(resultSet.getString("genre_name")))
                .build();
    }

    public void fillMpaTable() {
        if (jdbcTemplate.queryForList("SELECT MPA_ID FROM MPA;", Integer.class).isEmpty()) {
            String sql = "INSERT INTO MPA (MPA_ID, MPA_NAME) " +
                         "VALUES (?, ?);";
            for (int i = 1; i < 6; i++) {
                Mpa o = new Mpa(i);
                int t = jdbcTemplate.update(sql, i, o.getName());
            }
        }
    }
     public void fillGenreTable() {
         if (jdbcTemplate.queryForList("SELECT GENRE_ID FROM GENRE;", Integer.class).isEmpty()) {
             String sql = "INSERT INTO GENRE (GENRE_ID, GENRE_NAME) " +
                          "VALUES (?, ?);";
             for (int i = 1; i <= 6; i++) {
                 Genre o = new Genre(i);
                 jdbcTemplate.update(sql, i, o.getName().name());
             }
         }
     }

    public Map<String, Object> toMap(Film film) {
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