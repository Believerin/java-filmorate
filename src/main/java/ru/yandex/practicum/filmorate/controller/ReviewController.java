package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewLikeDislike;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(@Qualifier("Database") ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/reviews")
    public Review addReview(@Valid @RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping("/reviews")
    public Review editReview(@Valid @RequestBody Review review) {
        return reviewService.editReview(review);
    }

    @DeleteMapping("/reviews/{id}")
    public Review deleteReview(@PathVariable int id) {
        return reviewService.deleteReview(id);
    }

    @GetMapping("/reviews/{id}")
    public Review getReview(@PathVariable int id) {
        return reviewService.getReview(id);
    }

    @GetMapping("/reviews")
    //Получение всех отзывов по идентификатору фильма, если фильм не указан то все. Если кол-во не указано то 10.
    public List<Review> getReviewList(@RequestParam int filmId, @RequestParam(defaultValue = "10") int count) {
        return reviewService.getReviewList(filmId, count);
    }

    @PutMapping("/reviews/{id}/like/{userId}")
    public ReviewLikeDislike putLikeOnReview(@PathVariable int id, @PathVariable int userId) {
        return reviewService.putLikeOnReview(id, userId);
    }

    @PutMapping("/reviews/{id}/dislike/{userId}")
    public ReviewLikeDislike putDislikeOnReview(@PathVariable int id, @PathVariable int userId) {
        return reviewService.putDislikeOnReview(id, userId);
    }

    @DeleteMapping("/reviews/{id}/like/{userId}")
    public ReviewLikeDislike deleteLikeOnReview(@PathVariable int id, @PathVariable int userId) {
        return reviewService.deleteLikeOnReview(id, userId);
    }

    @DeleteMapping("/reviews/{id}/dislike/{userId}")
    public ReviewLikeDislike deleteDislikeReview(@PathVariable int id, @PathVariable int userId) {
        return reviewService.deleteDislikeOnReview(id, userId);
    }
}
