package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    @NotNull(message = "id не может быть равен null",
            groups = Marker.Update.class)
    Long id;

    @Email(message = "Некорректный email",
            groups = {Marker.Create.class, Marker.Update.class})
    @NotBlank(message = "Email не может быть пустым",
            groups = {Marker.Update.class, Marker.Create.class})
    String email;

    @NotBlank(message = "login не может быть пустым",
            groups = {Marker.Create.class, Marker.Update.class})
    String login;

    String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем.",
            groups = {Marker.Create.class, Marker.Update.class})
    LocalDate birthday;

    Set<Long> friends = new HashSet<>();
}
