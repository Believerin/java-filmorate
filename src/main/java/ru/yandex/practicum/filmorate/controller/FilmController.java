package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NoSuchBodyException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping
@Slf4j
public class FilmController {

    private final FilmService filmService;
    private final DirectorService directorService;

    @Autowired
    public FilmController(@Qualifier("Secondary") FilmService filmService, DirectorService directorService) {
        this.filmService = filmService;
        this.directorService = directorService;
    }

    @GetMapping("/films")
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        return filmService.getFilmById(id);
    }

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Film addLike (@PathVariable Integer id, @PathVariable Integer userId) {
        if (id <= 0 || userId <= 0) {
            throw new NoSuchBodyException(id < 0 & userId < 0 ? "id фильма и userId пользователя"
                    : id < 0 ? "id фильма" : "userId пользователя");
        }
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film deleteLike (@PathVariable Integer id, @PathVariable Integer userId) {
        if (id <= 0 || userId <= 0) {
            throw new NoSuchBodyException(id < 0 & userId < 0 ? "id фильма и userId пользователя"
                    : id < 0 ? "id фильма" : "userId пользователя");
        }
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getMostPopularFilms (@RequestParam(defaultValue = "10") int count) {
        if (count <= 0) {
            throw new NoSuchBodyException("count");
        }
        return filmService.getMostPopularFilms(count);
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMpa (@PathVariable Integer id) {
        if (id <= 0) {
            throw new NoSuchBodyException("id");
        }
        return filmService.getMpa(id);
    }

    @GetMapping("/mpa")
    public Collection<Mpa> findAllMpa() {
        return filmService.findAllMpa();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenre (@PathVariable Integer id) {
        if (id <= 0) {
            throw new NoSuchBodyException("id");
        }
        return filmService.getGenre(id);
    }

    @GetMapping("/genres")
    public Collection<Genre> findAllGenres() {
        return filmService.findAllGenres();
    }

    //------Эндпоинт для поиска фильмов режиссера по году/популярности
    @GetMapping("/films/director/{directorId}?sortBy=[year,likes]")
    public Collection<Film> getDirectorFilmsSortByYearOrLikes(@PathVariable int directorId, @RequestParam String sortBy)  {
        FilmService.FilmsByDirectorFinder directorExtension = (FilmService.FilmsByDirectorFinder) filmService;
        if (directorService.getDirectorById(directorId) == null) {
            throw new NoSuchBodyException("id");
        } else if (!sortBy.equals("year") & !sortBy.equals("likes")) {
            throw new NoSuchBodyException("sortBy");
        }
        switch (sortBy) {
            case "year":
                return directorExtension.getFilmsByDirectorSortByReleaseYear(directorId);
            case "likes":
                return directorExtension.getFilmsByDirectorSortByLikes(directorId);
            default:
                return List.of();
        }
    }
    //----------------------------------------------------------------
}