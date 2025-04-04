# Filmorate — сервис для поиска фильмов и общения

**Технологии проекта:**
- Java 11
- Spring Boot 3
- H2 Database
- JDBC
- Maven
- Lombok

Filmorate — это Java-приложение, которое позволяет пользователям:

- Хранить информацию о фильмах (название, описание, жанры, рейтинг MPA и т.д.)
- Ставить лайки фильмам и находить самые популярные
- Добавлять других пользователей в друзья и отслеживать их статус

---

## 💾 Структура базы данных

![Схема базы данных](docs/database-diagram.png)

### 📋 Таблицы:

#### `films`
- `id` — уникальный идентификатор фильма
- `name`, `description`, `release_date`, `duration`
- `mpa_id` — внешний ключ к рейтингу MPA

#### `users`
- `id` — уникальный ID пользователя
- `email`, `login`, `name`, `birthday`

#### `mpa_ratings`
- Список возрастных рейтингов (G, PG, PG-13, R, NC-17)

#### `genres`
- Все доступные жанры (комедия, драма, боевик и т.д.)

#### `film_genres`
- Связь многие-ко-многим между `films` и `genres`

#### `friendships`
- `user_id`, `friend_id` — пара пользователей
- `status_id` — статус дружбы

#### `film_likes`
- `film_id`, `user_id` — лайки пользователя фильму

---

## 💡 Примеры SQL-запросов

### Топ-5 популярных фильмов:
```sql
SELECT f.*
FROM films f
JOIN (
    SELECT film_id, COUNT(user_id) AS like_count
    FROM film_likes
    GROUP BY film_id
    ORDER BY like_count DESC
    LIMIT 5
) top_films ON f.id = top_films.film_id;
```

### Общие друзья двух пользователей:
```sql
SELECT u.*
FROM users u
JOIN friendships f1 ON u.id = f1.friend_id AND f1.user_id = 1
JOIN friendships f2 ON u.id = f2.friend_id AND f2.user_id = 2;
```

---

## 🚀 Запуск приложения
```bash
mvn clean install
mvn spring-boot:run
```

---

## 📁 Файл схемы базы данных
Расположение схемы: `docs/database-diagram.png`

---

## 🧬 schema.sql
```sql
-- Удаляем таблицы (если есть)
DROP TABLE IF EXISTS film_likes;
DROP TABLE IF EXISTS film_genres;
DROP TABLE IF EXISTS friendships;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS mpa_ratings;

-- Создаём таблицы
CREATE TABLE mpa_ratings (
    id INT PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);

CREATE TABLE genres (
    id INT PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);

CREATE TABLE films (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL CHECK (TRIM(name) <> ''),
    description VARCHAR(200),
    release_date DATE NOT NULL,
    duration INT CHECK (duration > 0),
    mpa_id INT,
    CONSTRAINT fk_mpa FOREIGN KEY (mpa_id) REFERENCES mpa_ratings(id)
);

CREATE TABLE users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    birthday DATE
);

CREATE TABLE friendships (
    user_id BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    status_id INT DEFAULT 1,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (friend_id) REFERENCES users(id)
);

CREATE TABLE film_likes (
    film_id BIGINT,
    user_id BIGINT,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE film_genres (
    film_id BIGINT,
    genre_id INT,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films(id),
    FOREIGN KEY (genre_id) REFERENCES genres(id)
);
```

---

## 💘 Интерфейс LikeStorage
```java
public interface LikeStorage {
    void addLike(Long filmId, Long userId);
    void removeLike(Long filmId, Long userId);
    List<Film> getTopLikedFilms(int count);
}
```

---

## 💾 LikeDbStorage
```java
@Repository
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getTopLikedFilms(int count) {
        String sql = "SELECT film_id FROM film_likes GROUP BY film_id ORDER BY COUNT(user_id) DESC LIMIT ?";
        List<Long> filmIds = jdbcTemplate.queryForList(sql, Long.class, count);

        return filmIds.stream()
                .map(filmStorage::getById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
```