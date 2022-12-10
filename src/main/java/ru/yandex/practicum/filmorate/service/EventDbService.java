package ru.yandex.practicum.filmorate.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.*;
import java.time.Instant;
import java.util.Collection;
import java.util.Objects;

@Component
public class EventDbService implements EventService{

    private final JdbcTemplate jdbcTemplate;

    public EventDbService(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Event createEvent(Event event){
            String sqlQuery = "INSERT INTO EVENTS (user_id, entity_id, event_type, operation, event_date) " +
                    "VALUES (?, ?, ?, ?, ?);";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"event_id"});
                ps.setInt(1,event.getUserId());
                ps.setInt(2, event.getEntityId());
                ps.setString(3,event.getEventType());
                ps.setString(4, event.getOperation());
                ps.setLong(5, event.getTimestamp().getEpochSecond());
                return ps;
            }, keyHolder);
            event.setEventId(keyHolder.getKey().intValue());
            return getAllEventsOfUser(event.getUserId()).stream()
                    .filter(o -> Objects.equals(o.getEventId(), event.getEventId()))
                    .findFirst()
                    .orElse(null);
        }

    @Override
    public Event get(int eventId){
        String sqlQuery = "SELECT * FROM EVENTS WHERE EVENT_ID =?;";
        return jdbcTemplate.queryForObject(sqlQuery, EventDbService::mapRowToEvent, eventId);
    }
    @Override
    public Collection<Event> getAllEventsOfUser(int userId){
        String sqlQuery = "SELECT * FROM EVENTS WHERE USER_ID = ?;";
        return jdbcTemplate.query(sqlQuery, EventDbService::mapRowToEvent, userId);
    }

    //............................ Служебные методы ..............................................

    private static Event mapRowToEvent(ResultSet resultSet, int rowNum) throws SQLException {
        return new Event(resultSet.getInt("event_id"),
                        resultSet.getInt("user_id"),
                        resultSet.getInt("entity_id"),
                        resultSet.getString("event_type"),
                        resultSet.getString("operation"),
                Instant.ofEpochSecond(resultSet.getLong("event_date")));
    }

}
