package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
@RequiredArgsConstructor
@EqualsAndHashCode
public class Review {
    Integer reviewId;
    String content;
    Boolean isPositive;
    Integer userId;
    Integer filmId;
    List<ReviewLikeDislike> likesDislikes;
    Integer useful; // рейтинг полезности

}
