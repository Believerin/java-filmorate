package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.status.Friendship;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
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
    @EqualsAndHashCode.Exclude
    private Map<Integer, Friendship> friends = new HashMap<>();

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public void setId() {
        this.id = nextId;
        nextId++;
    }

    public void setName(String name) {
        this.name = name;
    }

}