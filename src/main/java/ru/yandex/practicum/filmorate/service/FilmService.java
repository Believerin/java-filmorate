package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService implements FilmServing {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;


    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addLike (Integer filmId, Integer userId) {
        filmStorage.getFilms().get(filmId).getLikes().add(userStorage.getUsers().get(userId).getId());
        return filmStorage.getFilms().get(filmId);
    }

    public Film deleteLike (Integer filmId, Integer userId) {
        filmStorage.getFilms().get(filmId).getLikes().remove(userId);
        return filmStorage.getFilms().get(filmId);
    }

    public List<Film> getMostPopularFilms(int count) {
      return filmStorage.getFilms().values().stream()
              .sorted(Comparator.comparing(film -> -film.getLikes().size()))
              .limit(count)
              .collect(Collectors.toList());
    }
}