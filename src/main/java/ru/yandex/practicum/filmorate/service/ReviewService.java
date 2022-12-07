package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

 public interface ReviewService {
    Review addReview(Review review);

     Review editReview(Review review);

     Review deleteReview(int id);

     Review getReview(int id);

     List<Review> getReviewList(int id, int count);

     Review putLikeOnReview(int id, int userId);

     Review putDislikeOnReview(int id, int userId);

     Review deleteLikeOnReview(int id, int userId);

     Review deleteDislikeOnReview(int id, int userId);
}
