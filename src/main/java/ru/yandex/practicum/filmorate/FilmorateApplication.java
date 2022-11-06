package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class FilmorateApplication {
	static ConfigurableApplicationContext app;

	public static void main(String[] args) {
		app = SpringApplication.run(FilmorateApplication.class, args);
	}

	public static void stop() {
		app.stop();
	}
}
