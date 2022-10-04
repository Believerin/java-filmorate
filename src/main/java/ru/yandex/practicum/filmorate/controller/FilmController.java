package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.*;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
@Slf4j
@Getter
public class FilmController {

    Predicate <Film> requirementsPassed = film -> !film.getName().isBlank() // Для тестов (в них @Valid не работала)
            && film.getDescription().length() <= 200
            && film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))
            && film.getDuration() > 0;

    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        if (requirementsPassed.test(film)) {
            List<String> names = films.values().stream()
                    .map(Film::getName)
                    .collect(Collectors.toList());
            if (names.contains(film.getName())) {
                throw new ValidationException("Фильм уже существует");
            }
            film.setId();
            films.put(film.getId(), film);
            return film;
        } else {
            throw new ValidationException("Данные фильма не соответствуют критериям");
        }
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (requirementsPassed.test(film)) {
            if (films.containsKey(film.getId())) {
                films.put(film.getId(), film);
                return film;
            } else {
                throw new ValidationException("id обновляемого фильма отсутствует");
            }
        } else {
            throw new ValidationException("Данные фильма не соответствуют критериям");
        }
    }
}