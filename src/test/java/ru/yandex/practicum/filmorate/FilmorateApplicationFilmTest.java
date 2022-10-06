package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
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

import javax.validation.Valid;
import java.io.IOException;
import java.lang.reflect.Executable;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
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
	HttpClient httpclient;
	Gson gson;
	URI url = URI.create("http://localhost:8080/films");
	Type footype = new TypeToken<ArrayList<Film>>() {}.getType();
	HttpRequest GetRequest = HttpRequest
			.newBuilder()
			.uri(url)
			.GET()
			.version(HttpClient.Version.HTTP_1_1)
			.header("Content-Type", "application/json")
			.build();

	@BeforeAll
	public void create() {
	//	FilmorateApplication.main(new String[]{});
		gson = new GsonBuilder()
				.registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
				.create();
		httpclient = HttpClient.newHttpClient();
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
	void createAndUpdateFilm() throws IOException, InterruptedException {
		HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(film));
		HttpRequest request = HttpRequest
				.newBuilder()
				.uri(url)
				.POST(body)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-Type", "application/json")
				.build();
		HttpResponse<String> response = httpclient.send(request, HttpResponse.BodyHandlers.ofString());
		int f = response.statusCode();
		assertEquals(film, gson.fromJson(response.body(), Film.class), "Фильм не записан");

		filmUpdate.setId(1);
		body = HttpRequest.BodyPublishers.ofString(gson.toJson(filmUpdate));
		request = HttpRequest
				.newBuilder()
				.uri(url)
				.PUT(body)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-Type", "application/json")
				.build();
		response = httpclient.send(request, HttpResponse.BodyHandlers.ofString());
		f = response.statusCode();
		assertEquals(filmUpdate, gson.fromJson(response.body(), Film.class), "Фильм не обновлён");
	}

	@MethodSource("testSourceCreateFilm")
	@ParameterizedTest(name = "{index} Попытка добавления {0}")
	void createBadFilm(@Valid Film film, Integer id) throws IOException, InterruptedException {
		HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(film));
		HttpRequest request = HttpRequest
				.newBuilder()
				.uri(url)
				.POST(body)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-Type", "application/json")
				.build();
		httpclient.send(request, HttpResponse.BodyHandlers.ofString());
		HttpResponse<String> GetResponse = httpclient.send(GetRequest, HttpResponse.BodyHandlers.ofString());

		ArrayList<Film> films = gson.fromJson(GetResponse.body(), footype);
		assertFalse(films.contains(film), "Добавлен не соответствующий требованиям фильм");
	}

	@MethodSource({"testSourceCreateFilm", "testSourceUpdateFilm"})
	@ParameterizedTest(name = "{index} Попытка добавления {0}")
	void updateBadFilm(Film film, Integer id) throws IOException, InterruptedException {
		HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(film));
		HttpRequest request = HttpRequest
				.newBuilder()
				.uri(url)
				.PUT(body)
				.version(HttpClient.Version.HTTP_1_1)
				.header("Content-Type", "application/json")
				.build();
		httpclient.send(request, HttpResponse.BodyHandlers.ofString());
		HttpResponse<String> GetResponse = httpclient.send(GetRequest, HttpResponse.BodyHandlers.ofString());

		ArrayList<Film> films = gson.fromJson(GetResponse.body(), footype);
		assertFalse(films.contains(film), "Добавлен не соответствующий требованиям фильм");
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

class LocalDateAdapter extends TypeAdapter<LocalDate> {

	@Override
	public void write(final JsonWriter jsonWriter, final LocalDate localDate) throws IOException {
		jsonWriter.value(localDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
	}

	@Override
	public LocalDate read(final JsonReader jsonReader) throws IOException {
		return LocalDate.parse(jsonReader.nextString(), DateTimeFormatter.ISO_LOCAL_DATE);
	}
}