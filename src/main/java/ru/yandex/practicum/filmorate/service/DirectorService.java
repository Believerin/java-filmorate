package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface DirectorService {
    /**Вывести список всех режиссеров*/
    Collection<Director> findAllDirectors();
    /**Найти режиссера по DIRECTOR_ID*/
    Director getDirectorById(int directorId);
    /**Вывести список всех режиссеров, привязанных к конкретному фильму*/
    List<Director> getDirectorsOfFilm(int filmId);
    /**Добавить нового режиссера*/
    Director addNewDirector(Director director);
    /**Обновить данные режиссера*/
    Director updateDirector(Director director);
    /**Обновить информацию в таблице DIRECTORS_FILM*/
    void updateDirectorInFilm(Film film);
    /**Удалить режиссера по DIRECTOR_ID*/
    void removeDirector(int directorId);
    /**Связать режиссера и фильм в таблице DIRECTORS_FILM*/
    boolean connectFilmAndDirector(int filmId, int directorId);
    /**Удалить пару режиссер/фильм из таблицы DIRECTORS_FILM*/
    boolean disconnectFilmAndDirector(int filmId, int directorId);
    /**Вывести список фильмов режиссера DIRECTOR_ID, отсортированных по количеству лайков*/
    Collection<Film> getFilmsByDirectorSortByLikes(int directorId);
    /**Вывести список фильмов режиссера DIRECTOR_ID, отсортированных по году выпуска*/
    Collection<Film> getFilmsByDirectorSortByReleaseYear(int directorId);

}
