package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
public class User {

    @EqualsAndHashCode.Exclude
    private Integer id;
    @Email
    @NotNull
    @NotBlank
    private final String email;
    @NotNull
    @Pattern(regexp = "[^ ]*+") // Проверка на отсутствие пробела
    private final String login;

    private String name;
    @Past
    @NotNull
    private final LocalDate birthday;
    @NotNull
    private static Integer nextId = 1;

    public void setId() {
        this.id = nextId;
        nextId++;
    }

    public void setName(String name) {
        this.name = name;
    }

}