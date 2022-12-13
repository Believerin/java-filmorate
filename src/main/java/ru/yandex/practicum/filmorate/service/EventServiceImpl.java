package ru.yandex.practicum.filmorate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Primary
public class EventServiceImpl implements EventService{

    private final EventDbService eventDbService;

    @Autowired
    public EventServiceImpl(EventDbService eventDbService){
        this.eventDbService = eventDbService;
    }

    public Event saveEvent(String type, String operation, int id, int entityId){
        final Event event = addEvent(id);
        event.setEventType(type);
        event.setOperation(operation);
        event.setEntityId(entityId);
        return event;
    }

    public Event createEvent(Event event){
        return eventDbService.createEvent(event);
    }

    public Event getEvent(int eventId){
        return eventDbService.getEvent(eventId);
    }

    public List<Event> getAllEventsOfUser(int userId){
        return eventDbService.getAllEventsOfUser(userId).stream().collect(Collectors.toList());
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
