package ru.yandex.practicum.filmorate.service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;

public interface FilmService {

    Collection<Film> findAll();

    Film getFilmById(Integer id);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Mpa getMpa(int id);

    Genre getGenre (int id);

    Collection<Mpa> findAllMpa();

    Collection<Genre> findAllGenres();

    Film addLike (Integer filmId, Integer userId);

    Film deleteLike (Integer filmId, Integer userId);

    List<Film> getMostPopularFilms(int count);

    List<Film> getMostPopularFilmsByGenreOrYear(Integer count, Integer genreId, Integer year);

    List<Film> getCommonFilms(int userId, int friendId);

}