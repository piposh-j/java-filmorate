package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import java.time.LocalDate;

@Data
public class User {
    Long id;
    @Email(message = "Некорректный email")
    String email;
    @NotBlank
    String login;
    String name;
    @PastOrPresent
    LocalDate birthday;
}
