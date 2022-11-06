package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.NoSuchBodyException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.*;


@Component
@Qualifier("Secondary")
public class InMemoryFilmStorage implements FilmStorage {

    private final FilmStorage filmStorage;

    @Getter
    private final Map<Integer, Film> films = new HashMap<>();
    
    @Autowired
    public InMemoryFilmStorage(@Qualifier("priority") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public  Film getFilmById(Integer id) {
        Film film = filmStorage.getFilmById(id);
        if (film != null) {
            return film;
        } else {
            throw new NoSuchBodyException(String.format("Фильм с id %s отсутствует", id));
        }
    }

    public Film createFilm(Film film) {
        if (film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))) {
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
        if (film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))) {
            if (filmStorage.updateFilm(film) == null) {
                throw new NoSuchBodyException(String.format("Фильм с id %s отсутствует", film.getId()));
            } else {
                return getFilmById(film.getId());
            }
        } else {
            throw new ValidationException("Данные фильма не соответствуют критериям");
        }
    }

    public Mpa getMpa (int id) {
       return filmStorage.getMpa(id);
    }

    public Collection<Mpa> findAllMpa() {
        return filmStorage.findAllMpa();
    }

    @Override
    public Genre getGenre(int id) {
        return filmStorage.getGenre(id);
    }
    @Override
    public Collection<Genre> findAllGenres() {
        return filmStorage.findAllGenres();
    }
}