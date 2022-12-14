package ru.yandex.practicum.filmorate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Service
public class EventServiceImpl implements EventService{

    private final EventStorage eventStorage;

    @Autowired
    public EventServiceImpl(EventStorage eventStorage){
        this.eventStorage = eventStorage;
    }

    @Override
    public Event save(String type, String operation, int id, int entityId){
        final Event event = addEvent(id);
        event.setEventType(type);
        event.setOperation(operation);
        event.setEntityId(entityId);
        return event;
    }

    @Override
    public Event create(Event event){
        return eventStorage.create(event);
    }
    @Override
    public Event getEvent(int eventId){
        return eventStorage.get(eventId);
    }

    @Override
    public List<Event> getAllEventsOfUser(int userId){
        return new ArrayList<>(eventStorage.getAllEventsOfUser(userId));
    }

    //............................ Служебные методы ..............................................
    private Event addEvent(int userId){
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        Instant date = mapper.convertValue(Instant.now(), Instant.class);
        return new Event(userId, date);
    }
}
