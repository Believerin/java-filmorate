package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.status.FriendshipStatus;

@Data
@Builder
public class Friendship {

    private String userId;
    private String friendId;
    private FriendshipStatus status;
}