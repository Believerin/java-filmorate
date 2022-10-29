# java-filmorate
Template repository for Filmorate project.
![](/src/main/resources/ER.png)

Таблица "Friendship" - соединительная для User#1 и User#2 (friend). Имеет в качестве первичного ключа уникальную пару "user_id - friend_id".

Таблица "Genre_film" - соединительная для Film и Genre. Имеет в качестве первичного ключа уникальную пару "film_id - genre_id".

Таблица "Likes" - соединительная для User и Film. Имеет в качестве первичного ключа уникальную пару "film_id - user_id".

1. Запрос на получение общих друзей пользователей #1 и #2:
```  
SELECT friend_id
FROM Friendship as u
LEFT JOIN Friendship AS f ON u.friend_id = f.friend_id
WHERE u.user_id = 1 AND f.user_id = 2;
```
2. Запрос на получение ТОП-10 популярных фильмов:
```  
SELECT f.name
FROM Film AS f
LEFT JOIN Likes AS l ON f.film_id = l.film_id
ORDER BY COUNT(user_id) DESC
LIMIT 10;
```