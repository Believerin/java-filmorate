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
import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("priority")
public class FilmDbService implements FilmService {

    private final JdbcTemplate jdbcTemplate;
    private final DirectorService directorService;

    public FilmDbService(JdbcTemplate jdbcTemplate, DirectorService directorService) {
        this.jdbcTemplate = jdbcTemplate;
        this.directorService = directorService;
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
                        sqlFromGenreFilm, FilmDbService::mapRowToGenreFilm, film.getId())))
                .peek(film -> film.setDirectors(directorService.getDirectorsOfFilm(film.getId())))
                .collect(Collectors.toList());
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
            if (film != null) {
                film.setGenres(genres);
                //Добавление режиссера
                film.setDirectors(directorService.getDirectorsOfFilm(id));
                //Конец вставки
            }
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
        //Начало вставки
        //Проверяем наличие режиссера и добавляем
        if (film.getDirectors() != null) {
            int directorId = film.getDirectors().get(0).getId();
            Director director = directorService
                    .getDirectorById(directorId);
            if (director != null) {
                directorService.connectFilmAndDirector(film_id, directorId);
            }
        }
        //Конец вставки

        if (film.getGenres() != null) {

            StringBuilder sqlGenreFilm = new StringBuilder("INSERT INTO GENRE_FILM (FILM_ID, GENRE_ID) VALUES ");
            film.getGenres().stream()
                    .map(map -> map.get("id"))
                    .forEach(genreId -> sqlGenreFilm.append(String.format("(%d, %d), ", film_id, (int) genreId)));
            sqlGenreFilm.delete(sqlGenreFilm.length() - 2, sqlGenreFilm.length()).append(";");
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
            StringBuilder sqlGenreFilm = new StringBuilder("INSERT INTO GENRE_FILM (FILM_ID, GENRE_ID) VALUES ");
            film.getGenres().stream()
                    .distinct()
                    .map(map -> map.get("id"))
                    .forEach(genreId -> sqlGenreFilm.append(String.format("(%s, %s), ", film.getId(), genreId)));
            sqlGenreFilm.delete(sqlGenreFilm.length() - 2, sqlGenreFilm.length()).append(";");
            jdbcTemplate.update(String.valueOf(sqlGenreFilm));
        }
        //Начало вставки
        //Обновляем данные о режиссере фильма
        directorService.updateDirectorInFilm(film);
        //Конец вставки
        return doesExist ? getFilmById(film.getId()) : null;
    }

    public Mpa getMpa(int id) {
        String sql = "SELECT * " +
                "FROM MPA " +
                "WHERE MPA_ID = ?;";
        try {
            return jdbcTemplate.queryForObject(sql, FilmDbService::mapRowToMpa, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Collection<Mpa> findAllMpa() {
        String sql = "SELECT * " +
                "FROM MPA;";
        return jdbcTemplate.query(sql, FilmDbService::mapRowToMpa);
    }

    public Genre getGenre(int id) {
        String sql = "SELECT * " +
                "FROM GENRE " +
                "WHERE GENRE_ID = ?;";
        try {
             return jdbcTemplate.queryForObject(sql, FilmDbService::mapRowToGenre, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Collection<Genre> findAllGenres() {
        String sql = "SELECT * " +
                "FROM GENRE;";
        return jdbcTemplate.query(sql, FilmDbService::mapRowToGenre);
    }


    public Film addLike(Integer filmId, Integer userId) {
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
        String sql = "SELECT f.*, m.* " +
                "FROM FILM AS f " +
                "LEFT JOIN " +
                "LIKES AS l ON l.FILM_ID = f.FILM_ID " +
                "JOIN MPA AS m ON f.MPA_ID = m.MPA_ID " +
                "GROUP BY f.FILM_ID " +
                "ORDER BY COUNT(USER_ID) DESC " +
                "LIMIT ?;";
        return jdbcTemplate.query(sql, FilmDbService::mapRowToFilm, count).stream()
                .peek(film -> film.setGenres(jdbcTemplate.query(
                        sqlFromGenreFilm, FilmDbService::mapRowToGenreFilm, film.getId())
                        )).collect(Collectors.toList());
    }

    public List<Film> getMostPopularFilmsByGenreOrYear(Integer count, Integer genreId, Integer year) {
        String sqlFromGenreFilm = "SELECT gf.genre_id, g.genre_name " + "FROM genre_film AS gf " +
                "LEFT JOIN genre AS g ON g.genre_id = gf.genre_id " + "WHERE film_id = ?;";
        if (genreId != null) {
            String sql = "SELECT * " + "FROM film AS f " +
                    "JOIN (SELECT  * FROM genre_film) AS g_f ON (g_f.film_id = f.film_id AND g_f.genre_id = ?)" +
                    "LEFT JOIN " + "(SELECT * " + "FROM likes) AS l ON l.film_id = f.film_id " +
                    "JOIN mpa AS m ON f.mpa_id = m.mpa_id " + "GROUP BY f.film_id " + "ORDER BY COUNT(user_id) DESC " +
                    "LIMIT ?;";
            if (year == null) {
                return jdbcTemplate.query(sql, FilmDbService::mapRowToFilm, genreId, count).stream()
                        .peek(film -> film.setGenres(
                                jdbcTemplate.query(sqlFromGenreFilm, FilmDbService::mapRowToGenreFilm, film.getId())))
                        .collect(Collectors.toList());
            } else {
                return jdbcTemplate.query(sql, FilmDbService::mapRowToFilm, genreId, count).stream()
                        .peek(film -> film.setGenres(
                                jdbcTemplate.query(sqlFromGenreFilm, FilmDbService::mapRowToGenreFilm, film.getId())))
                        .filter(film -> film.getReleaseDate().getYear() == year).collect(Collectors.toList());
            }
        } else {
            String sql = "SELECT * " + "FROM film AS f " +
                    "LEFT JOIN " + "(SELECT * " + "FROM likes) AS l ON l.film_id = f.film_id " +
                    "JOIN mpa AS m ON f.mpa_id = m.mpa_id " + "GROUP BY f.film_id " + "ORDER BY COUNT(user_id) DESC " +
                    "LIMIT ?;";
            if (year == null) {
                return jdbcTemplate.query(sql, FilmDbService::mapRowToFilm, count).stream()
                        .peek(film -> film.setGenres(
                                jdbcTemplate.query(sqlFromGenreFilm, FilmDbService::mapRowToGenreFilm, film.getId())))
                        .collect(Collectors.toList());
            } else {
                return jdbcTemplate.query(sql, FilmDbService::mapRowToFilm, count).stream()
                        .peek(film -> film.setGenres(
                                jdbcTemplate.query(sqlFromGenreFilm, FilmDbService::mapRowToGenreFilm, film.getId())))
                        .filter(film -> film.getReleaseDate().getYear() == year).collect(Collectors.toList());
            }
        }

      }

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
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

    /*Эндпоинт для удаления пользователей*/
    @Override
    public Film delete(Integer filmId) {
        Film film = getFilmById(filmId);
        String sqlQuery = "DELETE FROM FILM WHERE film_id=?";
        jdbcTemplate.update(sqlQuery, filmId);
        return film;
    }
    /*Эндпоинт для удаления пользователей*/

    /**
     * Поиск фильмов по названию, по режиссеру или названию/режиссеру
     */
    @Override
    public List<Film> searchFilms(String query, boolean isDirector, boolean isTitle) {
        List<Integer> filmsId = List.of();
        List<Film> films = new ArrayList<>();
        String groupOrder = " GROUP BY f.FILM_ID " +
                "ORDER BY COUNT(l.USER_ID) DESC";
        StringBuilder sql = new StringBuilder().append("SELECT f.FILM_ID FROM FILM f " +
                "LEFT JOIN DIRECTORS_FILM df ON f.FILM_ID = df.FILM_ID " +
                "LEFT JOIN DIRECTORS d ON df.DIRECTOR_ID = d.DIRECTOR_ID " +
                "LEFT JOIN LIKES l ON f.FILM_ID = l.FILM_ID " +
                "WHERE ");
        if (isTitle & isDirector) {
            sql.append("REGEXP_LIKE(f.FILM_NAME, ?, 'i') OR REGEXP_LIKE(DIRECTOR_NAME, ?, 'i')");
            sql.append(groupOrder);
            filmsId = jdbcTemplate.queryForList(sql.toString(), Integer.class, query, query);
        } else if (isTitle) {
            sql.append("REGEXP_LIKE(f.FILM_NAME, ?, 'i')");
            sql.append(groupOrder);
            filmsId = jdbcTemplate.queryForList(sql.toString(), Integer.class, query);
        } else if (isDirector) {
            sql.append("REGEXP_LIKE(DIRECTOR_NAME, ?, 'i')");
            sql.append(groupOrder);
            filmsId = jdbcTemplate.queryForList(sql.toString(), Integer.class, query);
        }
        if (!filmsId.isEmpty()) {
            for (int i : filmsId) {
                films.add(getFilmById(i));
            }
        }
        return films;
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

    /**
     * Геттер для метода mapRowToFilm
     */
    public static Film mapRowToFilmGetter(ResultSet resultSet, int rowNum) throws SQLException {
        return mapRowToFilm(resultSet, rowNum);
    }

    /**
     * Геттер для метода mapRowToGenreFilm
     */
    public static Map<String, Object> mapRowToGenreFilmGetter(ResultSet resultSet, int rowNum) throws SQLException {
        return mapRowToGenreFilm(resultSet, rowNum);
    }

}
