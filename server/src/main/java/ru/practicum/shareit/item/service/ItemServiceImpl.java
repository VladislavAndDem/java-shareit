package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        log.info("IS -> пришли параметры userId - {} и ItemDto - {}", userId, itemDto);
        if (userId == 0) {
            log.error("InternalServerException - не указан заголовок и его значение");
            throw new InternalServerException("Id должен быть указан");
        }
        if (itemDto.getName().isBlank() || itemDto.getDescription().isBlank()) {
            log.error("InternalServerException - name или description не могут быть пустым");
            throw new InternalServerException("name или description должны быть указаны");
        }
        if (!userService.existsById(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        Item item = Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(User.builder().id(userId).build()) // Устанавливаем владельца по userId
                .build();
        if (itemDto.getRequestId() != null) {
            Request request = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос не найден."));
            item.setRequest(request);
        }
        log.info("Перед сохранением item");
        Item itemInDb = itemRepository.save(item);
        log.info("Item сохранен в бд {}", itemInDb);
        return ItemMapper.toItemDto(itemInDb);
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        log.info("IS -> пришли параметры на обновление oldItem userId - {}, itemId {} и ItemDto - {}", userId, itemId, itemDto);
        if (userId == 0) {
            log.error("InternalServerException - не указан заголовок и его значение");
            throw new InternalServerException("Id должен быть указан");
        }
        Optional<Item> oldItemOpt = itemRepository.findById(itemId);
        if (oldItemOpt.isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + itemId + " не найден");
        }
        Item oldItem = oldItemOpt.get();
        if (oldItem.getOwner().getId() != userId) {
            throw new NotFoundException("Вы не являетесть владельцем вещи");
        }

        if (itemDto.getName() != null) {
            oldItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            oldItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
        }

        if (itemDto.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
        }

        itemRepository.save(oldItem);
        return ItemMapper.toItemDto(oldItem);
    }

    @Override
    public ItemDto getItemByIdFromUser(long userId, long itemId) {
        Optional<Item> itemOpt = itemRepository.findById(itemId);

        if (itemOpt.isEmpty()) {
            log.info("NotFoundException - item не найден");
            throw new NotFoundException("Item с id " + itemId + " не найден");
        }
        ItemDto itemDto = ItemMapper.toItemDto(itemOpt.get());

        loadDetails(itemDto);
        return itemDto;
    }

    @Override
    public List<ItemDto> getAllItemFromUser(long userId) {
        List<Item> items = itemRepository.findAll();
        return items.stream()
                .filter(item -> item.getOwner().getId() == userId)
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    //public List<ItemDto> findAllByText(long userId, String text) {
    public List<ItemDto> findAllByText(String text) {
        log.info("Начался процесс поиска");
        if (text.isBlank()) {
            return List.of();
        }

        List<Item> items1 = itemRepository.findAllByText(text);

        return items1.stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }


    @Override
    public Item getItemOptionalById(long itemId) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            throw new NotFoundException("Item с id " + itemId + " не найден");
        }
        return itemOptional.get();
    }


    @Override
    public CommentDto saveComment(long userId, long itemId, CommentDto commentDto) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException("Вещь не найдена");
        }
        List<Booking> bookings = bookingRepository.findAllBookingByBookerIdAndItemId(userId, itemId);
        if (bookings.isEmpty()) {
            throw new NotFoundException("Вы не брали вещь в аренду");
        }
        List<Booking> bookings1 = bookings.stream()
                .filter(booking1 -> booking1.getBooker().getId().equals(userId))
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED))
                .toList();

        if (bookings1.isEmpty()) {
            throw new InternalServerException("Нельзя оствить отзыв, пока не завершена аренда и пока владалец вещи" +
                    " не одобрил бронирование");
        }

        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .item(item.get())
                .author(user.get())
                .created(LocalDateTime.now())
                .build();

        Comment commentInDb = commentRepository.save(comment);
        return CommentMapper.toCommentDto(commentInDb);
    }

    public void loadDetails(ItemDto itemDto) {
        List<Comment> comments = commentRepository.findAllByItemId(itemDto.getId());
        itemDto.setComments(comments.stream()
                .map(CommentMapper::toCommentDto)
                .toList());

        List<Booking> bookings = bookingRepository.findAllByItemOwnerId(itemDto.getId(),
                Sort.by(Sort.Direction.DESC, "start"));

        if (!bookings.isEmpty()) {
            Booking nextBooking = bookings.get(0);
            itemDto.setNextBooking(BookingMapper.toBookingDto(nextBooking));

            if (bookings.size() > 1) {
                itemDto.setLastBooking(BookingMapper.toBookingDto(bookings.get(1)));
            } else {
                itemDto.setLastBooking(BookingMapper.toBookingDto(nextBooking));
            }
        }
    }
}
