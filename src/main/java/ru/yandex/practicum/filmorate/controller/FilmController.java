package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.*;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }


    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        List<String> names = films.values().stream()
                .map(Film::getName)
                .collect(Collectors.toList());
        if (names.contains(film.getName())) {
            throw new ValidationException("Фильм уже существует");
        }
        if (film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))) {
            film.setId();
            films.put(film.getId(), film);
            return film;
        } else {
            throw new ValidationException("Данные фильма не соответствуют критериям");
        }
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))) {
            if (films.containsKey(film.getId())) {
                films.put(film.getId(), film);
                return film;
            } else {
                throw new ValidationException("Фильм" + film.getId() + "отсутствует");
            }
        } else {
            throw new ValidationException("Данные фильма не соответствуют критериям");
        }
    }
}