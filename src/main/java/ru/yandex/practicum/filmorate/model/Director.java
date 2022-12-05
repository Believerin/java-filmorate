package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode

public class Director {
    private int id;
    private String name;
}
