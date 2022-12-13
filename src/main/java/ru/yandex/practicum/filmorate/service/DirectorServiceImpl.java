package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DirectorServiceImpl implements DirectorService {
    private final DirectorStorage directorStorage;
    private final FilmService filmService;

    @Autowired
    public DirectorServiceImpl(DirectorStorage directorStorage, FilmService filmService) {
        this.directorStorage = directorStorage;
        this.filmService = filmService;
    }

    @Override
    public Collection<Director> findAllDirectors() {
        return directorStorage.findAllDirectors();
    }

    @Override
    public Director getDirectorById(int directorId) {
        return directorStorage.getDirectorById(directorId);
    }

    @Override
    public List<Director> getDirectorsOfFilm(int filmId) {
        return directorStorage.getDirectorsOfFilm(filmId);
    }

    @Override
    public Director addNewDirector(Director director) {
        return directorStorage.addNewDirector(director);
    }

    @Override
    public Director updateDirector(Director director) {
        return directorStorage.updateDirector(director);
    }

    @Override
    public void updateDirectorInFilm(Film film) {
        int filmId = film.getId();
        Optional<List<Director>> previousDirector = Optional.ofNullable(getDirectorsOfFilm(filmId));
        Optional<List<Director>> newDirector = Optional.ofNullable(film.getDirectors());

        if (previousDirector.isPresent()) {
            if (!previousDirector.get().isEmpty()) {
                directorStorage.disconnectFilmAndDirector(filmId, previousDirector.get().get(0).getId());
            }
        }

        if (newDirector.isPresent()) {
            if (!newDirector.get().isEmpty()) {
                directorStorage.connectFilmAndDirector(filmId, newDirector.get().get(0).getId());
            }
        }
    }

    @Override
    public boolean connectFilmAndDirector(int filmId, int directorId){
        return directorStorage.connectFilmAndDirector(filmId,directorId);
    }

    @Override
    public void removeDirector(int directorId) {
        directorStorage.removeDirector(directorId);
    }

    @Override
    public Collection<Film> getFilmsByDirectorSortByLikes(int directorId) {
        List<Integer> filmsId = directorStorage.getFilmsByDirectorSortByLikes();
        List<Film> filmsSortedByLikes = new ArrayList<>();
        if (filmsId.isEmpty()) {
            return directorStorage.getAllFilmsByDirector(directorId).stream()
                    .sorted(Comparator.comparingInt(Film::getId))
                    .collect(Collectors.toList());
        } else {
            for (Integer i : filmsId) {
                if (!getDirectorsOfFilm(i).isEmpty()) {
                    if (getDirectorsOfFilm(i).get(0).getId() == directorId) {
                        filmsSortedByLikes.add(filmService.getFilmById(i));
                    }
                }
            }
        }
        return filmsSortedByLikes;
    }

    @Override
    public Collection<Film> getFilmsByDirectorSortByReleaseYear(int directorId) {
        return directorStorage.getAllFilmsByDirector(directorId).stream()
                .sorted(Comparator.comparingInt(f -> f.getReleaseDate().getYear()))
                .collect(Collectors.toList());
    }
}
