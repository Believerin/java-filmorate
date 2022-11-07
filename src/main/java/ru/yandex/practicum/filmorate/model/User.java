package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(force=true)
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

    public void setName(String name) {
        this.name = name;
    }
}