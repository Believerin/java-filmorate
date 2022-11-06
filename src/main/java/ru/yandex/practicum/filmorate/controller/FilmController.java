package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NoSuchBodyException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmServing;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping
@Slf4j
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmServing filmServing;

    @Autowired
    public FilmController(@Qualifier("Secondary") FilmStorage filmStorage, @Qualifier("Secondary") FilmServing filmServing) {
        this.filmStorage = filmStorage;
        this.filmServing = filmServing;
    }

    @GetMapping("/films")
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        return filmStorage.getFilmById(id);
    }

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmStorage.createFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmStorage.updateFilm(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Film addLike (@PathVariable Integer id, @PathVariable Integer userId) {
        if (id <= 0 || userId <= 0) {
            throw new NoSuchBodyException(id < 0 & userId < 0 ? "id фильма и userId пользователя"
                    : id < 0 ? "id фильма" : "userId пользователя");
        }
        return filmServing.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film deleteLike (@PathVariable Integer id, @PathVariable Integer userId) {
        if (id <= 0 || userId <= 0) {
            throw new NoSuchBodyException(id < 0 & userId < 0 ? "id фильма и userId пользователя"
                    : id < 0 ? "id фильма" : "userId пользователя");
        }
        return filmServing.deleteLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getMostPopularFilms (@RequestParam(defaultValue = "10") int count) {
        if (count <= 0) {
            throw new NoSuchBodyException("count");
        }
        return filmServing.getMostPopularFilms(count);
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMpa (@PathVariable Integer id) {
        if (id <= 0) {
            throw new NoSuchBodyException("id");
        }
        return filmStorage.getMpa(id);
    }

    @GetMapping("/mpa")
    public Collection<Mpa> findAllMpa() {
        return filmStorage.findAllMpa();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenre (@PathVariable Integer id) {
        if (id <= 0) {
            throw new NoSuchBodyException("id");
        }
        return filmStorage.getGenre(id);
    }

    @GetMapping("/genres")
    public Collection<Genre> findAllGenres() {
        return filmStorage.findAllGenres();
    }
}