package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Marker;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @Validated(Marker.Create.class)
    @PostMapping
    public User create(@Valid @RequestBody User user) {
        validationUser(user);
        user.setId(getNextId());
        setName(user);
        users.put(user.getId(), user);
        return user;
    }

    @Validated(Marker.Update.class)
    @PutMapping
    public User update(@Valid @RequestBody User newUser) {

        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            validationUser(newUser);
            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setName(newUser.getName());
            oldUser.setBirthday(newUser.getBirthday());
            return newUser;
        }

        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
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
