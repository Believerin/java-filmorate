package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NoSuchBodyException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public Collection<Director> getAllDirectors() {
        return directorService.findAllDirectors();
    }

    @GetMapping("{directorId}")
    public Director findDirectorById(@PathVariable int directorId) {
        Director director = directorService.getDirectorById(directorId);
        if (director == null) {
            throw new NoSuchBodyException("directorId");
        }
        return director;
    }

    @PostMapping
    public Director createDirector(@RequestBody Director director) {
        return directorService.addNewDirector(director);
    }

    @PutMapping
    public void updateDirector(@RequestBody Director director) {
        directorService.updateDirector(director);
    }

    @DeleteMapping("{directorId}")
    public void deleteDirector(@PathVariable int directorId){
        directorService.removeDirector(directorId);
    }

}
