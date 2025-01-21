package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final InMemoryUserStorage inMemoryUserStorage;
    private final AtomicLong idGenerator = new AtomicLong(0); // Для автоинкремента

    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public Collection<User> findAll() {
        return inMemoryUserStorage.getUsers();
    }

    public User create(User user) {
        validationUser(user);
        long newId = idGenerator.incrementAndGet();
        user.setId(newId);
        setName(user);
        inMemoryUserStorage.put(newId, user);
        return user;
    }

    public User update(User newUser) {
        if (!inMemoryUserStorage.containsById(newUser.getId())) {
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        User oldUser = inMemoryUserStorage.get(newUser.getId());
        validationUser(newUser);
        oldUser.setEmail(newUser.getEmail());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setName(newUser.getName());
        oldUser.setBirthday(newUser.getBirthday());
        return newUser;

    }

    public User getUserById(Long id) {
        return inMemoryUserStorage.getUsers()
                .stream()
                .filter(it -> Objects.equals(it.getId(), id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    public User addFriend(long id, long friendId) {
        if (!inMemoryUserStorage.containsById(id)) {
            throw new NotFoundException("Пользователь с " + id + "не найден");
        }

        if (!inMemoryUserStorage.containsById(friendId)) {
            throw new NotFoundException("Пользователь с " + friendId + "не найден");
        }

        User user1 = inMemoryUserStorage.get(id);
        User user2 = inMemoryUserStorage.get(friendId);

        user1.getFriends().add(user2.getId());
        user2.getFriends().add(user1.getId());

        return user1;

    }

    public List<User> getUserFriends(long id) {
        if (!inMemoryUserStorage.containsById(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }

        User user = inMemoryUserStorage.get(id);

        return user.getFriends()
                .stream()
                .map(inMemoryUserStorage::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long id, long otherId) {
        if (!inMemoryUserStorage.containsById(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }

        if (!inMemoryUserStorage.containsById(otherId)) {
            throw new NotFoundException("Пользователь с " + otherId + "не найден");
        }

        User user1 = inMemoryUserStorage.get(id);
        User user2 = inMemoryUserStorage.get(otherId);

        Set<Long> commonIds = user1
                .getFriends()
                .stream()
                .filter(user2.getFriends()::contains)
                .collect(Collectors.toSet());

        return commonIds.stream()
                .map(inMemoryUserStorage::get)
                .collect(Collectors.toList());
    }

    public void removeFriend(long id, long friendId) {
        if (!inMemoryUserStorage.containsById(id)) {
            throw new NotFoundException("Пользователь с " + id + "не найден");
        }

        if (!inMemoryUserStorage.containsById(friendId)) {
            throw new NotFoundException("Пользователь с " + friendId + "не найден");
        }

        User user1 = inMemoryUserStorage.get(id);
        User user2 = inMemoryUserStorage.get(friendId);

        user1.getFriends().remove(friendId);
        user2.getFriends().remove(id);
    }


    private void validationUser(User user) {
        log.debug("Параметры нового пользователя. Электронная почта : {}," +
                        "Логин пользователя : {}," +
                        "Имя : {}," +
                        "Дата рождения  : {} ",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());

        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин содержать пробелы");
        }

    }

    private void setName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
