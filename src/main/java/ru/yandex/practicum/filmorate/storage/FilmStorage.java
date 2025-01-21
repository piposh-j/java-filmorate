package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film get(Long id);

    Film remove(Film user);

    Film put(long id, Film user);

    boolean containsById(Long id);

    Collection<Film> getFilms();
}
