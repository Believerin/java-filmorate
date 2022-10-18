package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Integer id) {
        return filmStorage.getFilm(id);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmStorage.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmStorage.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike (@PathVariable Integer id, @PathVariable Integer userId) {
        if (id <= 0 || userId <= 0) {
            throw new NullPointerException(id < 0 & userId < 0 ? "id и userId" : id < 0 ? "id" : "userId");
        }
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public Film deleteLike (@PathVariable Integer id, @PathVariable Integer userId) {
        if (id <= 0 || userId <= 0) {
            throw new NullPointerException(id < 0 & userId < 0 ? "id и userId" : id < 0 ? "id" : "userId");
        }
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("popular")
    public List<Film> getMostPopularFilms (@RequestParam(defaultValue = "10") int count) {
        if (count <= 0) {
            throw new NullPointerException("count");
        }
        return filmService.getMostPopularFilms(count);
    }
}