package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

@Setter
@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode

public class Director implements Serializable {
    @NotNull (message = "id can't be null")
    @Positive
    private int id;
    @NotBlank(message = "name can't be blank")
    @NotEmpty(message = "name can't be empty")
    @NotNull(message = "name can't be null")
    private String name;

    /**Валидатор данных тела запроса для контроллера*/
    public static boolean isValidDirector(Director director) {
        return !director.getName().isEmpty() &
                !director.getName().isBlank() &
                director.getId() > 0;
    }
    /**Метод для отображения строк. Передается в методы JdbcTemplate*/
    public static Director mapRowToDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getInt("DIRECTOR_ID"))
                .name(rs.getString("DIRECTOR_NAME"))
                .build();
    }
}
