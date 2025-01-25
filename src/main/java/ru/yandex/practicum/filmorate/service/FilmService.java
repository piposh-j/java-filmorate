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
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;
    private final UserService userService;
    private long idGenerator = 1L;
    private static final LocalDate MIN_VALID_DATE_FILM = LocalDate.of(1895, 12, 28);

    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage, UserService userService) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.userService = userService;
    }

    public Collection<Film> findAll() {
        return inMemoryFilmStorage.getFilms();
    }

    public Film create(Film film) {
        validationFilm(film);
        long newId = idGenerator++;
        film.setId(newId);
        inMemoryFilmStorage.put(newId, film);
        return film;
    }

    public Film update(Film newFilm) {
        validateFilmExists(newFilm.getId());

        Film oldFilm = inMemoryFilmStorage.get(newFilm.getId());
        validationFilm(newFilm);
        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());
        return newFilm;

    }

    public Film addLike(long id, long userId) {
        validateFilmExists(id);
        userService.validateUserExists(userId);

        Film film = inMemoryFilmStorage.get(id);
        film.getLikes().add(userId);
        return film;
    }

    public Film removeLike(long id, long userId) {
        validateFilmExists(id);
        userService.validateUserExists(userId);

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

    public void validateFilmExists(long id) {
        if (!inMemoryFilmStorage.containsById(id)) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
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
