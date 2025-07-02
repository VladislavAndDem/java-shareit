package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewUserDto {
    @NotBlank(message = "имя пользователя не может быть пустым")
    String name;

    @NotBlank(message = "Email пользователя не может быть пустым")
    @Email(message = "Email имеет неверный формат")
    String email;
}
