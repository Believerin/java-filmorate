package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewLikeDislike;

import java.util.List;

public interface ReviewService {
    Review addReview(Review review);

    Review editReview(Review review);

    Review deleteReview(int id);

    Review getReview(int id);

    List<Review> getReviews(int id, int count);

    ReviewLikeDislike putLikeOnReview(int id, int userId);

    ReviewLikeDislike putDislikeOnReview(int id, int userId);

    ReviewLikeDislike deleteLikeOnReview(int id, int userId);

    ReviewLikeDislike deleteDislikeOnReview(int id, int userId);
}
