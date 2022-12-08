package ru.yandex.practicum.filmorate.model;

import java.util.Comparator;

public class ReviewUsefulComparator implements Comparator<Review> {
    @Override
    public int compare(Review review1, Review review2) {

        return review2.getUseful().compareTo(review1.getUseful());
    }
}
