package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.status.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTest {

    private final UserStorage userService;
    private final FilmStorage filmService;

    User testUser;
    User testUpdatedUser;
    User testFriend;
    User testCommonFriend;

    Film testFilm;
    Film testUpdatedFilm;

    @BeforeAll
    public void create() {
        Map<String, Object> testMpa = new HashMap<>();
        testMpa.put("id", 2);
        Map<String, Object> testGenre = new HashMap<>();
        testGenre.put("id", 1);

        testFilm = Film.builder()
            .name("Good Detective")
            .description("film about crime")
            .duration(115)
            .releaseDate(LocalDate.of(1995, 1, 10))
            .mpa(testMpa)
            .genres(List.of(testGenre))
            .build();

        testUpdatedFilm = Film.builder()
                .name("Good Detective part two")
                .description("film about crime")
                .duration(120)
                .releaseDate(LocalDate.of(1997, 6, 17))
                .build();

        testUpdatedFilm = Film.builder()
            .name("Good Detective part two")
            .description("film about crime")
            .duration(120)
            .releaseDate(LocalDate.of(1997, 6, 17))
            .mpa(testMpa)
            .genres(List.of(testGenre))
            .build();

        testUser = User.builder()
                .name("Peter")
                .email("peter@rmail.com")
                .login("amateur")
                .birthday(LocalDate.of(1990, 9, 16))
                .build();

        testUpdatedUser = User.builder()
                .name("Peter")
                .email("peter@rmail.com")
                .login("pro")
                .birthday(LocalDate.of(1990, 9, 16))
                .build();

        testFriend = User.builder()
                .name("Mike")
                .email("mike@rmail.com")
                .login("mikechamp")
                .birthday(LocalDate.of(1988, 3, 28))
                .build();

        testCommonFriend = User.builder()
                .name("Nick")
                .email("nick@rmail.com")
                .login("nicktop")
                .birthday(LocalDate.of(1993, 10, 2))
                .build();

    }

    @Test
    @Order(1)
    public void testCreateUser() {
        User user = userService.createUser(testUser); // id = 1
        assertThat(user).isEqualTo(testUser);
    }

    @Test
    @Order(2)
    public void testGetUserById() {
        User user = userService.getUserById(1);
        assertThat(user).hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    @Order(3)
    public void testUpdateUser() {
        testUpdatedUser.setId(1);
        User user = userService.updateUser(testUpdatedUser);
        assertThat(user).hasFieldOrPropertyWithValue("login", "pro");
    }

    @Test
    @Order(4)
    public void testAddFriend() {
        userService.createUser(testFriend); // id = 2
        userService.addFriend(1, 2);
        Set<User> friend = userService.getAllFriends(1);
        assertThat(friend).isEqualTo((Set.of(testFriend)));
    }

    @Test
    @Order(5)
    public void testGetCommonFriends() {
        userService.createUser(testCommonFriend); // id = 3
        userService.addFriend(1, 3);
        userService.addFriend(2, 3);
        Set<User> commonFriends = userService.getCommonFriends(1, 2);
        assertThat(commonFriends).isEqualTo(Set.of(testCommonFriend));
    }

    @Test
    @Order(6)
    public void getAllFriends() {
        Set<User> friends = userService.getAllFriends(1);
        assertThat(friends).isEqualTo(Set.of(testFriend, testCommonFriend));
    }

    @Test
    @Order(7)
    public void testDeleteFriend() {
        userService.deleteFriend(1, 3);
        assertThat(userService.getAllFriends(1)).isEqualTo(Set.of(testFriend));
    }

    @Test
    @Order(8)
    public void TestFindAllUsers() {
        Set<User> users = new HashSet<>(userService.findAll());
        assertThat(users).isEqualTo(Set.of(testFriend, testUpdatedUser, testCommonFriend));
    }

    @Test
    @Order(9)
    public void testCreateFilm() {
        Film film = filmService.createFilm(testFilm); // id = 1
        assertThat(film).isEqualTo(testFilm);
    }

    @Test
    @Order(10)
    public void testGetFilmById() {
        Film film = filmService.getFilmById(1);
        assertThat(film).hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    @Order(11)
    public void testUpdateFilm() {
        testUpdatedFilm.setId(1);
        Film film = filmService.updateFilm(testUpdatedFilm);
        assertThat(film).hasFieldOrPropertyWithValue("name", "Good Detective part two");
    }

    @Test
    @Order(12)
    public void testAddLike() {
        filmService.addLike(1, 1);
        assertThat(filmService.getUsersLikesByFilmId(1)).isEqualTo((List.of(1)));
    }

    @Test
    @Order(13)
    public void TestGetMostPopularFilms() {
        List<Film> popular = filmService.getMostPopularFilms(1);
        assertThat(popular).isEqualTo(List.of(testUpdatedFilm));
    }

    @Test
    @Order(14)
    public void TestDeleteLike() {
        filmService.deleteLike(1, 1);
        assertThat(filmService.getUsersLikesByFilmId(1)).isNull();
    }

    @Test
    @Order(15)
    public void TestGetMpa() {
        Mpa mpa = filmService.getMpa(1);
        assertThat(mpa.getName()).isEqualTo("G");
    }

    @Test
    @Order(16)
    public void TestFindAllMpa() {
        List<Mpa> allMpa = new ArrayList<>(filmService.findAllMpa());
        List<Mpa> o = Stream.of(Rating.values())
                .map(Mpa::new)
                .collect(Collectors.toList());
        assertTrue(o.containsAll(allMpa));

    }

    @Test
    @Order(17)
    public void TestGetGenre() {
        Genre genre = filmService.getGenre(1);
        assertThat(genre.getName()).isEqualTo(Genres.Комедия);
    }

    @Test
    @Order(18)
    public void TestFindAllGenres() {
        List<Genre> allGenres =  new ArrayList<>(filmService.findAllGenres());
        List<Genre> o = Stream.of(Genres.values())
                .map(Genre::new)
                .collect(Collectors.toList());
        assertTrue(allGenres.containsAll(o));
    }
}