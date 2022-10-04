package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;
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

    UserController controller;

    @BeforeAll
    public void create() {
        controller = new UserController();
        user = new User("a@email.ru", "userLogin","user", LocalDate.of(1990, 6, 3));
        userUpdate = new User("abc@email.ru", "userLogin","user", LocalDate.of(1990, 6, 3));
        userBadEmail1 = new User("email.ru", "userLogin","user", LocalDate.of(1990, 6, 3));
        userBadEmail2 = new User("", "userLogin","user", LocalDate.of(1990, 6, 3));
        userBadLogin1 = new User("a@email.ru", "","user", LocalDate.of(1990, 6, 3));
        userBadLogin2 = new User("a@email.ru", "user Login","user", LocalDate.of(1990, 6, 3));
        userBadBirthDay1 = new User("a@email.ru", "userLogin","user", LocalDate.of(2025, 6, 3));
    }

    @Test
    void createAndUpdateUser() {
        controller.createUser(user);
        assertEquals(user, controller.getUsers().get(1), "Пользователь не записан");
        userUpdate.setId(1);
        controller.updateUser(userUpdate);
        assertEquals(userUpdate, controller.getUsers().get(1), "Пользователь не обновлён");
    }

    @MethodSource("testSourceCreateUser")
    @ParameterizedTest(name = "{index} Попытка добавления {0}")
    void createUser(User user, Integer id) {
        assertThrows(
                ValidationException.class,
                () -> controller.createUser(user), "Исключение не выбрасывается");
        assertFalse(controller.getUsers().containsValue(user), "Добавлен не соответствующий требованиям фильм");
        System.out.println(user.getId());
    }

    @MethodSource({"testSourceCreateUser", "testSourceUpdateUser"})
    @ParameterizedTest(name = "{index} Попытка добавления {0}")
    void updateUser(User user, Integer id) {
        user.setId(id);
        assertThrows(
                ValidationException.class,
                () -> controller.updateUser(user), "Исключение не выбрасывается");
        assertFalse(controller.getUsers().containsValue(user), "Добавлен не соответствующий требованиям фильм");
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