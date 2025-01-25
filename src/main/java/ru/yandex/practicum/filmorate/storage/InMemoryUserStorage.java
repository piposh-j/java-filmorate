package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {

    private Map<Long, User> users = new HashMap<>();

    @Override
    public User get(Long id) {
        return users.get(id);
    }

    @Override
    public User remove(User user) {
        return users.remove(user);
    }

    @Override
    public User put(long id, User user) {
        return users.put(id, user);
    }

    @Override
    public boolean containsById(Long id) {
        return users.containsKey(id);
    }

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public List<User> getUsersByIds(Set<Long> ids) {
        return ids.stream()
                .map(this::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
