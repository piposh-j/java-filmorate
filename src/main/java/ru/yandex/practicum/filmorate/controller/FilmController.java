package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Marker;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Validated
@RestController
@RequestMapping("/films")
public class FilmController {

    private static final LocalDate MIN_VALID_DATE_FILM = LocalDate.of(1895, 12, 28);
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @Validated(Marker.Create.class)
    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validationFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Validated(Marker.Update.class)
    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            validationFilm(newFilm);
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            return newFilm;
        }

        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = films
                .keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
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
