package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;
    private final AtomicLong idGenerator = new AtomicLong(0); // Для автоинкремента
    private static final LocalDate MIN_VALID_DATE_FILM = LocalDate.of(1895, 12, 28);

    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public Collection<Film> findAll() {
        return inMemoryFilmStorage.getFilms();
    }

    public Film create(Film film) {
        validationFilm(film);
        long newId = idGenerator.incrementAndGet();
        film.setId(newId);
        inMemoryFilmStorage.put(newId, film);
        return film;
    }

    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        if (inMemoryFilmStorage.containsById(newFilm.getId())) {
            Film oldFilm = inMemoryFilmStorage.get(newFilm.getId());
            validationFilm(newFilm);
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            return newFilm;
        }

        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    public Film addLike(long id, long userId) {
        if (!inMemoryFilmStorage.containsById(id)) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }

        if (!inMemoryUserStorage.containsById(userId)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }

        Film film = inMemoryFilmStorage.get(id);
        film.getLikes().add(userId);
        return film;
    }

    public Film removeLike(long id, long userId) {
        if (!inMemoryFilmStorage.containsById(id)) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }

        if (!inMemoryUserStorage.containsById(userId)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }

        Film film = inMemoryFilmStorage.get(id);
        film.getLikes().remove(userId);
        return film;
    }

    public List<Film> getPopularFilms(int count) {
        return inMemoryFilmStorage.getFilms()
                .stream()
                .sorted((film1, film2) -> Integer.compare(film2.getLikes().size(), film1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validationFilm(Film film) {
        log.debug("Параметры нового фильма. Название : {}," +
                        "Описание : {}," +
                        "Продолжительность : {}," +
                        "Дата релиза фильма : {} ",
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getReleaseDate());

        if (film.getReleaseDate().isBefore(MIN_VALID_DATE_FILM)) {
            throw new ValidationException("Дата релиза фильма должна быть — не раньше 28 декабря 1895 года");
        }
    }
}
