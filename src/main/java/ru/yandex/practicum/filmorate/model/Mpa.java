package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.status.Rating;

@Setter
@Getter
@Builder
@RequiredArgsConstructor
@EqualsAndHashCode
public class Mpa {

    private final int id;
    private final String name;

    public Mpa(Rating rating) {
        name = rating.toString().equals( "PG13") ? "PG-13" : rating.toString().equals("NC17") ? "NC-17" : rating.toString();
        this.id = rating.getMpaId();
    }
}