package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface UserStorage {
    User get(Long id);

    User remove(User user);

    User put(long id, User user);

    boolean containsById(Long id);

    Collection<User> getUsers();

    List<User> getUsersByIds(Set<Long> ids);
}
