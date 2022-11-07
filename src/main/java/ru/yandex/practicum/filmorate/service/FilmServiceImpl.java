package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoSuchBodyException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;

import java.util.Collection;
import java.util.List;

import static ru.yandex.practicum.filmorate.model.Film.CINEMA_START;

@Service
@Qualifier("Secondary")
public class FilmServiceImpl implements FilmService {
    
    private final FilmDbService filmDbService;

    @Autowired
    public FilmServiceImpl(FilmDbService filmDbService) {

        this.filmDbService = filmDbService;
    }

    public Collection<Film> findAll() {
        return filmDbService.findAll();
    }

    public  Film getFilmById(Integer id) {
        Film film = filmDbService.getFilmById(id);
        if (film != null) {
            return film;
        } else {
            throw new NoSuchBodyException(String.format("Фильм с id %s отсутствует", id));
        }
    }

    public Film createFilm(Film film) {
        if (film.getReleaseDate().isAfter(CINEMA_START)) {
            Film o = filmDbService.createFilm(film);
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
            if (filmDbService.updateFilm(film) == null) {
                throw new NoSuchBodyException(String.format("Фильм с id %s отсутствует", film.getId()));
            } else {
                return filmDbService.getFilmById(film.getId());
            }
        } else {
            throw new ValidationException("Данные фильма не соответствуют критериям");
        }
    }

    public Mpa getMpa (int id) {
        return filmDbService.getMpa(id);
    }

    public Collection<Mpa> findAllMpa() {
        return filmDbService.findAllMpa();
    }

    @Override
    public Genre getGenre(int id) {
        return filmDbService.getGenre(id);
    }
    @Override
    public Collection<Genre> findAllGenres() {
        return filmDbService.findAllGenres();
    }

    public Film addLike (Integer filmId, Integer userId) {
        filmDbService.addLike(filmId, userId);
        return filmDbService.getFilmById(filmId);
    }

    public Film deleteLike (Integer filmId, Integer userId) {
        filmDbService.deleteLike(filmId, userId);
        return filmDbService.getFilmById(filmId);
    }

    public List<Film> getMostPopularFilms(int count) {
      return filmDbService.getMostPopularFilms(count);
    }
}