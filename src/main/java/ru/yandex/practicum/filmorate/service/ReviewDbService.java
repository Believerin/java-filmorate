package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoSuchBodyException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewLikeDislike;
import ru.yandex.practicum.filmorate.model.ReviewUsefulComparator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Qualifier("Database")
public class ReviewDbService implements ReviewService {
    protected final FilmService filmService;
    protected final UserService userService;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDbService(JdbcTemplate jdbcTemplate, @Qualifier("priority") FilmService filmService,
                           @Qualifier("priority") UserService userService) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmService = filmService;
        this.userService = userService;
    }

    private static Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {

        return Review.builder().reviewId(resultSet.getInt("review_id")).userId(resultSet.getInt("user_id"))
                .filmId(resultSet.getInt("film_id")).content(resultSet.getString("content"))
                .isPositive(resultSet.getBoolean("is_positive")).useful(resultSet.getInt("useful")).build();
    }

    @Override
    public Review addReview(Review review) {

        if (userService.getUserById(review.getUserId()) == null ||
                filmService.getFilmById(review.getFilmId()) == null) {
            throw new NoSuchBodyException("ресурс");
        }

        String query =
                "INSERT INTO reviews (user_id, film_id, content, is_positive, useful)" + " VALUES(?, ?, ?, ?, 0)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        review.setUseful(0);
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(query, new String[]{"review_id"});
            stmt.setInt(1, review.getUserId());
            stmt.setInt(2, review.getFilmId());
            stmt.setString(3, review.getContent());
            stmt.setBoolean(4, review.getIsPositive());
            return stmt;
        }, keyHolder);

        review.setReviewId(keyHolder.getKey().intValue());

        return review;
    }

    @Override
    public Review editReview(Review review) {
        checkIfReviewExists(review.getReviewId());

        String query = "UPDATE reviews " + "SET content = ?, is_positive = ? " + "WHERE review_id = ?;";
        jdbcTemplate.update(query, review.getContent(), review.getIsPositive(), review.getReviewId());
        return review;
    }

    @Override
    public Review deleteReview(int id) {
        Review review = checkIfReviewExists(id);
        String sql = "DELETE " + "FROM users_reviews_like_dislike " + "WHERE review_id = ?;";
        jdbcTemplate.update(sql, id);
        String sql2 = "DELETE " + "FROM reviews " + "WHERE review_id = ?;";
        jdbcTemplate.update(sql2, id);
        return review;
    }

    @Override
    public Review getReviewById(int id) {
        return checkIfReviewExists(id);
    }

    public List<Review> getAllReviewsForFilmId(Integer filmId) {
        String query;
        if (filmId == null) {
            query = "SELECT * FROM reviews";
            return jdbcTemplate.query(query, ReviewDbService::mapRowToReview);
        } else {
            query = "SELECT * FROM reviews WHERE reviews.film_id = ?";
            return jdbcTemplate.query(query, ReviewDbService::mapRowToReview, filmId);
        }
    }

    @Override
    public List<Review> getReviews(Integer filmId, Integer count) {
        return this.getAllReviewsForFilmId(filmId).stream().sorted(new ReviewUsefulComparator()).limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewLikeDislike putLikeOnReview(int id, int userId) {
        String query = "INSERT INTO users_reviews_like_dislike(user_id, review_id, islike)  " + "VALUES (?, ?, TRUE)";
        jdbcTemplate.update(query, userId, id);
        String queryLike = "UPDATE reviews SET useful = useful  + 1 WHERE review_id = ?";
        jdbcTemplate.update(queryLike, id);
        return new ReviewLikeDislike(id, userId, true);
    }

    @Override
    public ReviewLikeDislike putDislikeOnReview(int id, int userId) {
        String query = "INSERT INTO users_reviews_like_dislike" + " (user_id, review_id, islike)  VALUES (?, ?, FALSE)";
        jdbcTemplate.update(query, userId, id);
        String queryLike = "UPDATE reviews SET useful = useful  - 1 WHERE review_id = ?";
        jdbcTemplate.update(queryLike, id);
        return new ReviewLikeDislike(id, userId, false);
    }

    @Override
    public ReviewLikeDislike deleteLikeOnReview(int id, int userId) {
        String query = "DELETE FROM users_reviews_like_dislike"
                + " WHERE user_id = ? AND review_id = ? AND islike = TRUE";
        jdbcTemplate.update(query, userId, id);
        String queryLike = "UPDATE reviews SET useful = useful  - 1 WHERE review_id = ?";
        jdbcTemplate.update(queryLike, id);
        return new ReviewLikeDislike(id, userId, true);
    }

    @Override
    public ReviewLikeDislike deleteDislikeOnReview(int id, int userId) {
        String query = "DELETE FROM users_reviews_like_dislike" +
                " WHERE user_id = ? AND review_id = ? AND islike = FALSE";
        jdbcTemplate.update(query, userId, id);
        String queryLike = "UPDATE reviews SET useful = useful  + 1 WHERE review_id = ?";
        jdbcTemplate.update(queryLike, id);
        return new ReviewLikeDislike(id, userId, false);
    }

    private Review checkIfReviewExists(int reviewId) throws NoSuchBodyException {
        String query = "SELECT * FROM reviews WHERE review_id = ?";
        return jdbcTemplate.query(query, ReviewDbService::mapRowToReview, reviewId).stream().findAny()
                .orElseThrow(() -> new NoSuchBodyException(("Review not found")));
    }
}
