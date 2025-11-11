package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.NewRequestDto;

@RestController
@RequestMapping(path = "/requests")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RequestController {
    RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Valid @RequestBody NewRequestDto newRequestDto) {
        return requestClient.createRequest(userId, newRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsById(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestClient.getAllRequestsById(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(name = "size", defaultValue = "50") @Positive Integer size) {
        return requestClient.getRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable("requestId") Long requestId) {
        return requestClient.getRequestById(userId, requestId);
    }
}