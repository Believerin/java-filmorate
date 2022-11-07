package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.status.FriendshipStatus;

@Getter
@Setter
@EqualsAndHashCode
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Friendship {

    private String userId;
    private String friendId;
    private FriendshipStatus status;
}