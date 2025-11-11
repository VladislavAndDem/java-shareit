package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.NewRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static Request toRequest(NewRequestDto request) {
        return Request.builder()
                .description(request.getDescription())
                .created(LocalDateTime.now())
                .build();
    }

    public static RequestDto toRequestDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requestorName(request.getRequestor().getName())
                .created(request.getCreated())
                .build();
    }
}