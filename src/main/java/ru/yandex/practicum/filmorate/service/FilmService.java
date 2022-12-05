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

    /**Вложенный интерфейс для:
     * 1) Вывода списка фильмов режиссера, отсортированного по году/лайкам
     * 2) Добавления/удаления режиссера из фильма*/
    interface DirectorManager {
        /**Вывести список фильмов режиссера DIRECTOR_ID, отсортированных по количеству лайков*/
        Collection<Film> getFilmsByDirectorSortByLikes(int directorId);
        /**Вывести список фильмов режиссера DIRECTOR_ID, отсортированных по году выпуска*/
        Collection<Film> getFilmsByDirectorSortByReleaseYear(int directorId);
        /**Добавить режиссера в фильм*/
        void addDirector(Film film);
        /**Удалить режиссера из фильма*/
        void removeDirector(Film film);
        /**Обновить данные о режиссере фильма*/
        void updateDirector(Film film);

    }
}