package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;

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

    public void setId() {
        this.id = nextId;
        nextId++;
    }
}