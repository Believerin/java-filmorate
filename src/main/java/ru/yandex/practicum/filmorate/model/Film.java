package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.*;

@Data
@Builder
public class Film {

    final public static LocalDate CINEMA_START = LocalDate.of(1895, 12, 28);

    @EqualsAndHashCode.Exclude
    private Integer id;
    @NotBlank
    @NotNull()
    private final String  name;
    @Size(max = 200)
    @NotNull
    private final String description;
    @NotNull
    private final LocalDate releaseDate;
    @Positive
    private final Integer duration;
    @NotNull
    private static Integer nextId = 1;
    @EqualsAndHashCode.Exclude
    @NotNull
    private Map<String, Object> mpa;
    @EqualsAndHashCode.Exclude

    private List<Map<String, Object>> genres;

    public void setId() {
        this.id = nextId;
        nextId++;
    }
}