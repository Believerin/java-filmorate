package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;
import java.time.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @NotNull
    private int eventId;
    @NotNull
    private int userId;
    private int entityId;
    private String eventType;
    private String operation;
   // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Instant timestamp;

    public Event(int userId, Instant timestamp) {
        this. userId = userId;
        this.timestamp = timestamp;
    }
}
