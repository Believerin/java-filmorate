package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorService {

    public Collection<Director> findAllDirectors();
    public Director getDirectorById(int directorId);
    public Director addNewDirector(Director director);
    public void updateDirector(Director director);
    public void removeDirector(int directorId);

}
