package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Long, Film> films = new HashMap<>();

    @Override
    public Film get(Long id) {
        return films.get(id);
    }

    @Override
    public Film remove(Film film) {
        return films.remove(film);
    }

    @Override
    public Film put(long id, Film film) {
        return films.put(id, film);
    }

    @Override
    public boolean containsById(Long id) {
        return films.containsKey(id);
    }

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }
}
