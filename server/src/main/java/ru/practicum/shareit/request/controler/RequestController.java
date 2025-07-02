package ru.practicum.shareit.request.controler;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.NewRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class RequestController {

    RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@RequestBody NewRequestDto request,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Принят запрос от пользователя с ID: {} на создание запроса вещи: {}", userId, request);
        return requestService.create(request, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getRequest(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Принят запрос от пользователя с ID: {} на получение списка своих запросов", userId);
        return requestService.getAllRequestsById(userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @RequestParam(name = "size", defaultValue = "50") Integer size) {
        log.debug("Принят запрос от пользователя с ID: {} на получение списка всех запросов, с учетом пагинации", userId);
        return requestService.findAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public RequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable("requestId") Long requestId) {
        log.debug("Принят запрос от пользователя с ID: {} на получение информации о запросе вещи с ID: {}", userId, requestId);
        return requestService.findById(userId, requestId);
    }
}