package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmServing {

    Film addLike (Integer filmId, Integer userId);

    Film deleteLike (Integer filmId, Integer userId);

    List<Film> getMostPopularFilms(int count);
}