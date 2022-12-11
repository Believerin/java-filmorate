DROP TABLE IF EXISTS
    mpa, film, film_user, genre, friendship, likes, genre_film, DIRECTORS, DIRECTORS_FILM, reviews, users_reviews_like_dislike;

CREATE TABLE IF NOT EXISTS mpa (
    mpa_id int PRIMARY KEY,
    mpa_name varchar
);
CREATE TABLE IF NOT EXISTS film (
    film_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_name varchar,
    description varchar(200),
    release_date date,
    duration int,
    mpa_id int REFERENCES mpa (mpa_id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS film_user (
    user_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email varchar UNIQUE,
    login varchar UNIQUE,
    name varchar,
    birthday date
);
CREATE TABLE IF NOT EXISTS genre (
    genre_id int PRIMARY KEY,
    genre_name varchar
);
CREATE TABLE IF NOT EXISTS friendship (
    user_id int REFERENCES film_user (user_id) ON DELETE CASCADE,
    friend_id int REFERENCES film_user (user_id) ON DELETE CASCADE,
    status varchar,
    CONSTRAINT friendship_pk PRIMARY KEY (user_id, friend_id)
);
CREATE TABLE IF NOT EXISTS likes (
    film_id int REFERENCES film (film_id) ON DELETE CASCADE,
    user_id int REFERENCES film_user (user_id) ON DELETE CASCADE,
    CONSTRAINT likes_pk PRIMARY KEY (film_id, user_id)
);
CREATE TABLE IF NOT EXISTS genre_film (
    film_id int REFERENCES film (film_id) ON DELETE CASCADE,
    genre_id int REFERENCES genre (genre_id) ON DELETE CASCADE,
    CONSTRAINT genre_film_pk PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS directors
(
    director_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    director_name varchar
);

CREATE TABLE IF NOT EXISTS directors_film
(
    director_id integer REFERENCES directors (director_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    film_id     integer REFERENCES film (film_id),
    CONSTRAINT directors_film_pk
        PRIMARY KEY (director_id, film_id)
);

CREATE TABLE IF NOT EXISTS reviews
(
    review_id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id     int NOT NULL,
    film_id     int NOT NULL,
    content     varchar(200),
    useful      int,
    is_positive boolean,

    UNIQUE (user_id, film_id),
    FOREIGN KEY (film_id) REFERENCES film (film_id),
    FOREIGN KEY (user_id) REFERENCES film_user (user_id)
);


CREATE TABLE IF NOT EXISTS users_reviews_like_dislike
(
    user_id      int NOT NULL,
    review_id int NOT NULL,
    isLike boolean,
    FOREIGN KEY (user_id) REFERENCES film_user (user_id),
    FOREIGN KEY (review_id) REFERENCES reviews (review_id),
    UNIQUE (user_id, review_id)
);

CREATE TABLE IF NOT EXISTS events(
    event_id    INTEGER PRIMARY KEY AUTO_INCREMENT,
    user_id     INTEGER REFERENCES FILM_USER(user_id),
    entity_id   INTEGER,
    event_type  VARCHAR(50),
    operation   VARCHAR(50),
    event_date  LONG
);
