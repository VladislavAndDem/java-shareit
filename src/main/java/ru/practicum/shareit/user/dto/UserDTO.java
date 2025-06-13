package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.beans.ConstructorProperties;

@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;

    @ConstructorProperties({"id", "name"})
    public UserDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
