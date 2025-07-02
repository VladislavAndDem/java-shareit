package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewRequestDto {
    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 1000, message = "Описание не должно больше 1000 символов")
    String description;
}