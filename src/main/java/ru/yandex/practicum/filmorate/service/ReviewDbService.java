package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoSuchBodyException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewLikeDislike;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Component
@Qualifier("Database")
public class ReviewDbService implements ReviewService {
    private final JdbcTemplate jdbcTemplate;

    public ReviewDbService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review addReview(Review review) {
        String query = "INSERT INTO reviews (user_id, film_id, content, is_positive)" + " VALUES(?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

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
        String query = "UPDATE reviews " + "SET user_id = ?, film_id = ?, content = ?, is_positive = ? " +
                "WHERE review_id = ?;";
        jdbcTemplate.update(query, review.getUserId(), review.getFilmId(), review.getContent(), review.getIsPositive(),
                review.getReviewId());
        return review;
    }

    @Override
    public Review deleteReview(int id) {
        Review review = checkIfReviewExists(id);
        String sql = "DELETE " + "FROM reviews " + "WHERE review_id = ?;";
        jdbcTemplate.update(sql, id);
        return review;
    }

    @Override
    public Review getReview(int id) {
        return checkIfReviewExists(id);
    }

    @Override
    public List<Review> getReviewList(int id, int count) {
        return Collections.emptyList();
    }

    @Override
    public ReviewLikeDislike putLikeOnReview(int userId, int reviewId) {
        String query =
                "INSERT INTO users_reviews_like_dislike" + " (user_id, review_id, islike) " + "VALUES (?, ?, TRUE)";
        jdbcTemplate.update(query, userId, reviewId);
        return new ReviewLikeDislike(userId, reviewId, true);
    }

    @Override
    public ReviewLikeDislike putDislikeOnReview(int userId, int reviewId) {
        String query =
                "INSERT INTO users_reviews_like_dislike" + " (user_id, review_id, islike) " + "VALUES (?, ?, FALSE)";
        jdbcTemplate.update(query, userId, reviewId);
        return new ReviewLikeDislike(userId, reviewId, false);
    }

    @Override
    public ReviewLikeDislike deleteLikeOnReview(int userId, int reviewId) {

        String query =
                "DELETE FROM users_reviews_like_dislike"
                        + " WHERE user_id = ? AND review_id = ? AND islike = TRUE";
        jdbcTemplate.update(query, userId, reviewId);
        return new ReviewLikeDislike(userId, reviewId, true);
    }

    @Override
    public ReviewLikeDislike deleteDislikeOnReview(int userId, int reviewId) {

        String query =
                "DELETE FROM users_reviews_like_dislike"
                        + " WHERE user_id = ? AND review_id = ? AND islike = FALSE";
        jdbcTemplate.update(query, userId, reviewId);
        return new ReviewLikeDislike(userId, reviewId, false);
    }

    private static Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {

        return Review.builder().reviewId(resultSet.getInt("review_id")).userId(resultSet.getInt("user_id"))
                .filmId(resultSet.getInt("film_id")).content(resultSet.getString("content"))
                .isPositive(resultSet.getBoolean("is_positive")).build();
    }

    private Review checkIfReviewExists(int reviewId) throws NoSuchBodyException {
        String query = "SELECT * FROM reviews WHERE review_id = ?";
        return jdbcTemplate.query(query, ReviewDbService::mapRowToReview, reviewId).stream().findAny()
                .orElseThrow(() -> new NoSuchBodyException(("Film not found")));
    }
}
