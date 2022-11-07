package ru.yandex.practicum.filmorate.status;

public enum Rating {
    G(1),
    PG(2),
    PG13(3),
    R(4),
    NC17(5);

    private final int mpaId;
    Rating(int mpaId) {
        this.mpaId = mpaId;
    }

    public int getMpaId() {
        return mpaId;
    }
}