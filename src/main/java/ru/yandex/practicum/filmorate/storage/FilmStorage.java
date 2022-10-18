package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.*;

import java.util.Collection;
import java.util.Map;

public interface FilmStorage {

    Collection<Film> findAll();

    Film getFilm(Integer id);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Map<Integer, Film> getFilms();
}