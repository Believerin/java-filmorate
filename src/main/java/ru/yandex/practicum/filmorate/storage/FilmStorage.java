package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.*;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> findAll();

    Film getFilmById(Integer id);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Mpa getMpa(int id);

    Genre getGenre (int id);

    Collection<Mpa> findAllMpa();

    Collection<Genre> findAllGenres();
}