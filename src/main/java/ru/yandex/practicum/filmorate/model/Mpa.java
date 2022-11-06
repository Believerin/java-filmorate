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

    public Mpa(int id) {
        this.id = id;
        Rating r = id == 1 ? Rating.G : id == 2 ? Rating.PG : id == 3 ? Rating.PG13 : id == 4 ? Rating.R : Rating.NC17;
        name = r.toString().equals( "PG13") ? "PG-13" : r.toString().equals("NC17") ? "NC-17" : r.toString();
    }
}