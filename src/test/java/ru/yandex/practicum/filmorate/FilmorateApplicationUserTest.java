package ru.yandex.practicum.filmorate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class FilmorateApplicationUserTest {

    User user;
    User userUpdate;
    User userBadEmail1;
    User userBadEmail2;
    User userBadLogin1;
    User userBadLogin2;
    User userBadBirthDay1;
    HttpClient httpclient;
    Gson gson;
    URI url = URI.create("http://localhost:8080/users");
    Type footype = new TypeToken<ArrayList<Film>>() {}.getType();
    HttpRequest GetRequest = HttpRequest
            .newBuilder()
            .uri(url)
            .GET()
            .version(HttpClient.Version.HTTP_1_1)
            .header("Content-Type", "application/json")
            .build();

    UserController controller;

    @BeforeAll
    public void create() {
        FilmorateApplication.main(new String[]{});
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
        httpclient = HttpClient.newHttpClient();
        user = new User("a@email.ru", "userLogin","user", LocalDate.of(1990, 6, 3));
        userUpdate = new User("abc@email.ru", "userLogin","user", LocalDate.of(1990, 6, 3));
        userBadEmail1 = new User("email.ru", "userLogin","user", LocalDate.of(1990, 6, 3));
        userBadEmail2 = new User("", "userLogin","user", LocalDate.of(1990, 6, 3));
        userBadLogin1 = new User("a@email.ru", "","user", LocalDate.of(1990, 6, 3));
        userBadLogin2 = new User("a@email.ru", "user Login","user", LocalDate.of(1990, 6, 3));
        userBadBirthDay1 = new User("a@email.ru", "userLogin","user", LocalDate.of(2025, 6, 3));
    }

    @AfterAll
    public void stop() {
        FilmorateApplication.stop();
    }

    @Test
    void createAndUpdateUser() throws IOException, InterruptedException {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(user));
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(body)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = httpclient.send(request, HttpResponse.BodyHandlers.ofString());
        int f = response.statusCode();
        assertEquals(user, gson.fromJson(response.body(), User.class), "Пользователь не записан");

        userUpdate.setId(1);
        body = HttpRequest.BodyPublishers.ofString(gson.toJson(userUpdate));
        request = HttpRequest
                .newBuilder()
                .uri(url)
                .PUT(body)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        response = httpclient.send(request, HttpResponse.BodyHandlers.ofString());
        f = response.statusCode();
        assertEquals(userUpdate, gson.fromJson(response.body(), User.class), "Пользователь не обновлён");
    }

    @MethodSource("testSourceCreateUser")
    @ParameterizedTest(name = "{index} Попытка добавления {0}")
    void createBadUser(User user, Integer id) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(user));
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(body)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        httpclient.send(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> GetResponse = httpclient.send(GetRequest, HttpResponse.BodyHandlers.ofString());

        ArrayList<User> users = gson.fromJson(GetResponse.body(), footype);
        assertFalse(users.contains(user), "Добавлен не соответствующий требованиям пользователь");
    }

    @MethodSource({"testSourceCreateUser", "testSourceUpdateUser"})
    @ParameterizedTest(name = "{index} Попытка добавления {0}")
    void updateBadUser(User user, Integer id) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(user));
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .PUT(body)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        httpclient.send(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> GetResponse = httpclient.send(GetRequest, HttpResponse.BodyHandlers.ofString());

        ArrayList<User> users = gson.fromJson(GetResponse.body(), footype);
        assertFalse(users.contains(user), "Добавлен не соответствующий требованиям фильм");
    }

    private Stream<Arguments> testSourceCreateUser () {
        return Stream.of(
                Arguments.of(userBadEmail1, null),
                Arguments.of(userBadEmail2, null),
                Arguments.of(userBadLogin1, null),
                Arguments.of(userBadLogin2, null),
                Arguments.of(userBadBirthDay1, null)
        );
    }
    private Stream<Arguments> testSourceUpdateUser () {
        return Stream.of(
                Arguments.of(user, 999)
        );
    }
}