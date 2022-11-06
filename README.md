# java-filmorate
Template repository for Filmorate project.

![](/src/main/resources/ER.png)

Таблица "Friendship" - соединительная для User#1 и User#2 (friend). Имеет в качестве первичного ключа уникальную пару "user_id - friend_id".

Таблица "Genre_film" - соединительная для Film и Genre. Имеет в качестве первичного ключа уникальную пару "film_id - genre_id".

Таблица "Likes" - соединительная для User и Film. Имеет в качестве первичного ключа уникальную пару "film_id - user_id".

1. Запрос на получение общих друзей пользователей #1 и #2:
```  
SELECT USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY
FROM FILM_USER
WHERE USER_ID IN (
SELECT FRIEND_ID
FROM FRIENDSHIP
WHERE USER_ID = ? AND FRIEND_ID IN (SELECT FRIEND_ID FROM FRIENDSHIP WHERE USER_ID = ?));
```
2. Запрос на получение ТОП-10 популярных фильмов:
```  
SELECT gf.GENRE_ID, g.GENRE_NAME
FROM GENRE_FILM AS gf
LEFT JOIN GENRE AS g ON g.GENRE_ID = gf.GENRE_ID
WHERE FILM_ID = ?;
```