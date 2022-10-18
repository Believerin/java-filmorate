package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.NoSuchBodyException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;


import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    @Getter
    private final Map<Integer, Film> films = new HashMap<>();

    public Collection<Film> findAll() {
        return films.values();
    }

    public Film getFilm(Integer id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new NoSuchBodyException(String.format("Фильм с id %s отсутствует", id));
        }
    }

    public Film createFilm(Film film) {
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

    public Film updateFilm(Film film) {
        if (film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))) {
            if (films.containsKey(film.getId())) {
                films.put(film.getId(), film);
                return film;
            } else {
                throw new NoSuchBodyException(String.format("Фильм с id %s отсутствует", film.getId()));
            }
        } else {
            throw new ValidationException("Данные фильма не соответствуют критериям");
        }
    }
}
