package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class FilmorateApplicationFilmTest {

	Film film;
	Film filmUpdate;
	Film filmBadName;
	Film filmBadDesc1;
	Film filmBadRelease1;
	Film filmBadRelease2;
	Film filmBadDuration1;
	Film filmBadDuration2;
	FilmController controller;

	@BeforeAll
	public void create() {
		controller = new FilmController();
		StringBuilder initial = new StringBuilder();
		initial.setLength(201);
		String description = initial.toString();
		film = new Film("film", "drama", LocalDate.of(2001, 6, 3), 90);
		filmUpdate = new Film("film2", "thriller", LocalDate.of(2001, 6, 3), 90);
		filmBadName = new Film("", "drama", LocalDate.of(2001, 6, 3), 90);
		filmBadDesc1 = new Film("filmBadDesc1", description, LocalDate.of(2001, 6, 3), 90);
		filmBadRelease1 = new Film("filmBadRelease1", "drama", LocalDate.of(1890, 6, 3), 90);
		filmBadRelease2 = new Film("filmBadRelease2", "drama", Film.CINEMA_START, 90);
		filmBadDuration1 = new Film("filmBadDuration1", "drama", LocalDate.of(2001, 6, 3), -90);
		filmBadDuration2 = new Film("filmBadDuration2", "drama", LocalDate.of(2001, 6, 3), 0);
	}
	@Test
	void createAndUpdateFilm() {
		controller.createFilm(film);
		assertEquals(film, controller.getFilms().get(1), "Фильм не записан");
		filmUpdate.setId(1);
		controller.updateFilm(filmUpdate);
		assertEquals(filmUpdate, controller.getFilms().get(1), "Фильм не обновлён");
	}

	@MethodSource("testSourceCreateFilm")
	@ParameterizedTest(name = "{index} Попытка добавления {0}")
	void createBadFilm(Film film, Integer id) {
		assertThrows(
				ValidationException.class,
				() -> controller.createFilm(film), "Исключение не выбрасывается");
		assertFalse(controller.getFilms().containsValue(film), "Добавлен не соответствующий требованиям фильм");
		System.out.println(film.getId());
	}

	@MethodSource({"testSourceCreateFilm", "testSourceUpdateFilm"})
	@ParameterizedTest(name = "{index} Попытка добавления {0}")
	void updateBadFilm(Film film, Integer id) {
		film.setId(id);
		assertThrows(
				ValidationException.class,
				() -> controller.updateFilm(film), "Исключение не выбрасывается");
		assertFalse(controller.getFilms().containsValue(film), "Добавлен не соответствующий требованиям фильм");
	}

	private Stream<Arguments> testSourceCreateFilm () {
		return Stream.of(
				Arguments.of(filmBadName, null),
				Arguments.of(filmBadDesc1, null),
				Arguments.of(filmBadRelease1, null),
				Arguments.of(filmBadRelease2, null),
				Arguments.of(filmBadDuration1, null),
				Arguments.of(filmBadDuration2, null)
		);
	}
	private Stream<Arguments> testSourceUpdateFilm () {
		return Stream.of(
				Arguments.of(film, 999)
		);
	}
}