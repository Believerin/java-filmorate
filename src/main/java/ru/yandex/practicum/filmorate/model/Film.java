package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@EqualsAndHashCode
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(force=true)
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
    @EqualsAndHashCode.Exclude
    @NotNull
    private Map<String, Object> mpa;
    @EqualsAndHashCode.Exclude
    private List<Map<String, Object>> genres;

    //Добавление нового поля director.
    @EqualsAndHashCode.Exclude
    private List<Map <String, Integer>> directors;
}