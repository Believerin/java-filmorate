package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.status.Rating;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
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
    @NonNull
    private Set<Integer> likes = new HashSet<>();
    @EqualsAndHashCode.Exclude
    private Set<String> genre;
    @EqualsAndHashCode.Exclude
    private Rating rating;

    public void setId() {
        this.id = nextId;
        nextId++;
    }
}