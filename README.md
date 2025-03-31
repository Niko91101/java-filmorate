# Filmorate — сервис для поиска фильмов и общения

Filmorate — это Java-приложение, которое позволяет пользователям:

-  Хранить информацию о фильмах (название, описание, жанры, рейтинг MPA и т.д.)
-  Ставить лайки фильмам и находить самые популярные
-  Добавлять других пользователей в друзья и отслеживать их статус (подтверждён/неподтверждён)

---

## Структура базы данных

![Схема базы данных](docs/database-diagram.png)

###  Таблицы:

#### `films`
- `id` — уникальный идентификатор фильма
- `name`, `description`, `release_date`, `duration`
- `mpa_rating_id` — внешний ключ к рейтингу MPA

#### `users`
- `id` — уникальный ID пользователя
- `email`, `login`, `name`, `birthday`

#### `mpa_ratings`
- Список возрастных рейтингов (G, PG, PG-13, R, NC-17)

#### `genres`
- Все доступные жанры (комедия, драма, боевик и т.д.)

#### `film_genres`
- Связь «многие ко многим» между `films` и `genres`

#### `friendship_statuses`
- Статусы дружбы (подтверждена, не подтверждена)

#### `friendships`
- Пользователь → Друг → Статус дружбы

---

## Примеры SQL-запросов

### Получить топ-5 фильмов по лайкам:
```sql
SELECT f.*
FROM films f
JOIN (
    SELECT film_id, COUNT(user_id) as like_count
    FROM film_likes
    GROUP BY film_id
    ORDER BY like_count DESC
    LIMIT 5
) as top_films ON f.id = top_films.film_id;
```

###  Найти общих друзей двух пользователей:
```sql
SELECT u.*
FROM users u
JOIN friendships f1 ON u.id = f1.friend_id AND f1.user_id = 1
JOIN friendships f2 ON u.id = f2.friend_id AND f2.user_id = 2;
```

---

## Как запустить
```bash
mvn clean install
mvn spring-boot:run
```

---

## Расположение схемы
Картинка схемы: `docs/filmorate_schema.png`
