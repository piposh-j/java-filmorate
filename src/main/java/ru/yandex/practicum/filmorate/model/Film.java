package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
public class Film {

    @NotNull(message = "id не может быть равен null",
            groups = Marker.Update.class)
    Long id;

    @NotBlank(message = "Название фильма не может быть пустым",
            groups = {Marker.Create.class, Marker.Update.class})
    String name;

    @Size(max = 200, message = "Максимальная длина описания фильма — 200 символов",
            groups = {Marker.Create.class, Marker.Update.class})
    String description;

    LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом.",
            groups = {Marker.Create.class, Marker.Update.class})
    int duration;


    Set<Long> likes = new HashSet<>();
}
