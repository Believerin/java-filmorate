package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Component
@Qualifier("Database")
public class ReviewDbService implements ReviewService {
    @Override
    public Review addReview(Review review) {
        return null;
    }

    @Override
    public Review editReview(Review review) {
        return null;
    }

    @Override
    public Review deleteReview(int id) {
        return null;
    }

    @Override
    public Review getReview(int id) {
        return null;
    }

    @Override
    public List<Review> getReviewList(int id, int count) {
        return null;
    }

    @Override
    public Review putLikeOnReview(int id, int userId) {
        return null;
    }

    @Override
    public Review putDislikeOnReview(int id, int userId) {
        return null;
    }

    @Override
    public Review deleteLikeOnReview(int id, int userId) {
        return null;
    }

    @Override
    public Review deleteDislikeOnReview(int id, int userId) {
        return null;
    }
}
