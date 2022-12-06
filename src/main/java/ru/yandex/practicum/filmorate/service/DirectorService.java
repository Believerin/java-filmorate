package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorService {

    Collection<Director> findAllDirectors();
    Director getDirectorById(int directorId);
    Director addNewDirector(Director director);
    Director updateDirector(Director director);
    void removeDirector(int directorId);
    boolean connectDirectorAndFilm(int filmId, int directorId);
    boolean disconnectDirectorAndFilm(int filmId, int directorId);

}
