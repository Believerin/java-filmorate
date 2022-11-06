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

    public Genre(int id) {
        this.id = id;
        name = id == 1 ? Genres.Комедия : id == 2 ? Genres.Драма : id == 3 ? Genres.Мультфильм : id == 4
                ? Genres.Фантастика : id == 5 ? Genres.Документальный : Genres.Остросюжетный;

    }
}