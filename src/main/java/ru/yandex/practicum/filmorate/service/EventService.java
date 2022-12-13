package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;
import java.util.List;

public interface EventService {
    Event saveEvent(String type, String operation, int id, int entityId);
    Event createEvent(Event event);
    Event getEvent(int eventId);
    List<Event> getAllEventsOfUser(int userId);

}
