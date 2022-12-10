package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;

public interface EventService {
    Event createEvent(Event event);
    Event get(int eventId);
    Collection<Event> getAllEventsOfUser(int userId);

}
