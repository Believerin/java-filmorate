package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoSuchBodyException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewLikeDislike;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final FilmService filmService;
    private final UserService userService;
    private final EventService eventService;
    private final ReviewStorage reviewStorage;

    @Autowired
    public ReviewServiceImpl(FilmService filmService, UserService userService, EventServiceImpl eventService, ReviewStorage reviewStorage) {
        this.filmService = filmService;
        this.userService = userService;
        this.eventService = eventService;
        this.reviewStorage = reviewStorage;
    }

    @Override
    public Review addReview(Review review) {
        if (userService.getUserById(review.getUserId()) == null ||
                filmService.getFilmById(review.getFilmId()) == null) {
            throw new NoSuchBodyException("ресурс");
        }
        Review ansReview = reviewStorage.addReview(review);
        final Event event = eventService.save("REVIEW", "ADD", ansReview.getUserId(), ansReview.getReviewId());
        eventService.create(event);
        return ansReview;

    }

    @Override
    public Review editReview(Review review) {
        Review ansReview = reviewStorage.editReview(review);
        final Event event = eventService.save("REVIEW", "UPDATE", ansReview.getUserId(), review.getReviewId());
        eventService.create(event);
        return ansReview;
    }

    @Override
    public Review deleteReview(int id) {
        Review ansReview = reviewStorage.deleteReview(id);
        final Event event = eventService.save("REVIEW", "REMOVE", ansReview.getUserId(), ansReview.getReviewId());
        eventService.create(event);
        return ansReview;
    }

    @Override
    public Review getReviewById(int id) {
        return reviewStorage.getReviewById(id);
    }

    @Override
    public List<Review> getReviews(Integer id, Integer count) {
        return reviewStorage.getAllReviewsForFilmId(id).stream()
                .sorted((Comparator.comparingInt(Review::getUseful)).reversed()).limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewLikeDislike putLikeOnReview(int id, int userId) {
        return reviewStorage.putLikeOnReview(id,userId);
    }

    @Override
    public ReviewLikeDislike putDislikeOnReview(int id, int userId) {
        return reviewStorage.putDislikeOnReview(id,userId);
    }

    @Override
    public ReviewLikeDislike deleteLikeOnReview(int id, int userId) {
        return reviewStorage.deleteLikeOnReview(id,userId);
    }

    @Override
    public ReviewLikeDislike deleteDislikeOnReview(int id, int userId) {
        return reviewStorage.deleteDislikeOnReview(id,userId);
    }
}
