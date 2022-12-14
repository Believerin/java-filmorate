package ru.yandex.practicum.filmorate.status;

public enum Genres {
    Комедия(1),
    Драма(2),
    Мультфильм(3),
    Триллер(4),
    Документальный(5),
    Боевик(6);

    private final int genresId;
    Genres(int genresId) {
        this.genresId = genresId;
    }

    public int getGenresId() {
        return genresId;
    }
}