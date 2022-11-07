package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.status.Genres;

@Setter
@Getter
@Builder
@RequiredArgsConstructor
@EqualsAndHashCode
public class Genre {

    private final int id;
    private final Genres name;

    public Genre(Genres genres) {
        name = genres;
        this.id = genres.getGenresId();
    }
}