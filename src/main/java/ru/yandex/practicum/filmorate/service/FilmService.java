package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.List;

@Service
@Qualifier("Secondary")
public class FilmService implements FilmServing {

    private final FilmStorage filmStorage;
    private final FilmServing filmServing;

    @Autowired
    public FilmService(@Qualifier("priority") FilmStorage filmStorage, @Qualifier("priority") FilmServing filmServing) {
        this.filmStorage = filmStorage;
        this.filmServing = filmServing;
    }

    public Film addLike (Integer filmId, Integer userId) {
        filmServing.addLike(filmId, userId);
        return filmStorage.getFilmById(filmId);
    }

    public Film deleteLike (Integer filmId, Integer userId) {
        filmServing.deleteLike(filmId, userId);
        return filmStorage.getFilmById(filmId);
    }

    public List<Film> getMostPopularFilms(int count) {
      return filmServing.getMostPopularFilms(count);
    }
}