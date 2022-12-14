package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoSuchBodyException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;

import static ru.yandex.practicum.filmorate.model.Film.CINEMA_START;

@Service
@Primary
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final EventServiceImpl eventService;

    @Autowired
    public FilmServiceImpl(FilmStorage filmStorage, EventServiceImpl eventService) {
        this.filmStorage = filmStorage;
        this.eventService = eventService;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film getFilmById(Integer id) {
        Film film = filmStorage.getFilmById(id);
        if (film != null) {
            return film;
        } else {
            throw new NoSuchBodyException(String.format("Фильм с id %s отсутствует", id));
        }
    }

    public Film createFilm(Film film) {
        if (film.getReleaseDate().isAfter(CINEMA_START)) {
            Film o = filmStorage.createFilm(film);
            if (o == null) {
                throw new ValidationException("Фильм уже существует");
            } else {
                return o;
            }
        } else {
            throw new ValidationException("Данные фильма не соответствуют критериям");
        }
    }

    public Film updateFilm(Film film) {
        if (film.getReleaseDate().isAfter(CINEMA_START)) {
            if (filmStorage.updateFilm(film) == null) {
                throw new NoSuchBodyException(String.format("Фильм с id %s отсутствует", film.getId()));
            } else {
                return filmStorage.getFilmById(film.getId());
            }
        } else {
            throw new ValidationException("Данные фильма не соответствуют критериям");
        }
    }

    public Mpa getMpa (int id) {
        Mpa mpa = filmStorage.getMpa(id);
        if (mpa != null) {
            return mpa;
        } else {
            throw new NoSuchBodyException(String.format("Mpa с id %s отсутствует", id));
        }
    }

    public Collection<Mpa> findAllMpa() {
        return filmStorage.findAllMpa();
    }

    @Override
    public Genre getGenre(int id) {
        Genre genre = filmStorage.getGenre(id);
        if (genre != null) {
            return genre;
        } else {
            throw new NoSuchBodyException(String.format("Genre с id %s отсутствует", id));
        }
    }

    @Override
    public Collection<Genre> findAllGenres() {
        return filmStorage.findAllGenres();
    }

    public Film addLike(Integer filmId, Integer userId) {
        filmStorage.addLike(filmId, userId);
        final Event event = eventService.save("LIKE", "ADD", userId, filmId);
        eventService.create(event);
        return filmStorage.getFilmById(filmId);
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        filmStorage.deleteLike(filmId, userId);
        final Event event = eventService.save("LIKE", "REMOVE", userId, filmId);
        eventService.create(event);
        return filmStorage.getFilmById(filmId);
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.getMostPopularFilms(count);
    }

    @Override
    public List<Film> getMostPopularFilmsByGenreOrYear(Integer count, Integer genreId, Integer year) {
        return filmStorage.getMostPopularFilmsByGenreOrYear(count, genreId,  year);
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    /*Эндпоинт для удаления пользователей*/
    @Override
    public Film delete(Integer filmId) {
        Film film = filmStorage.delete(filmId);
        if (film == null) {
            throw new NoSuchBodyException(String.format("Фильм с id %s отсутствует", filmId));
        } else {
            return film;
        }
    }
    /*Эндпоинт для удаления пользователей*/

    @Override
    public List<Film> searchFilms(String query, boolean isDirector, boolean isTitle) {
        return filmStorage.searchFilms(query, isDirector, isTitle);
    }

}