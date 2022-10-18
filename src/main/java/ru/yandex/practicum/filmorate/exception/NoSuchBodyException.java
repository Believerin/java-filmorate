package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class NoSuchBodyException extends RuntimeException {

    private final String parameter;

    public NoSuchBodyException(String parameter) {
        this.parameter = parameter;
    }
}