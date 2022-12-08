package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Setter
@Getter
@Builder
//@RequiredArgsConstructor
@EqualsAndHashCode
public class Review {

    Integer reviewId;
    @NotNull
    String content;
    @NotNull
    Boolean isPositive;
    @NotNull
    Integer userId;
    @NotNull
    Integer filmId;
    //List<ReviewLikeDislike> likesDislikes;
    Integer useful; // рейтинг полезности

}
