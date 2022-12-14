package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventService {
    Event save(String type, String operation, int id, int entityId);
    Event create(Event event);
    Event getEvent(int eventId);
    List<Event> getAllEventsOfUser(int userId);

}
