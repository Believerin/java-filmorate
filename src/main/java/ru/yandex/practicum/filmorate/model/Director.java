package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@Builder
@RequiredArgsConstructor
@EqualsAndHashCode

public class Director implements Serializable {

    private final int id;
    private final String name;

}
