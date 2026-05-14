package com.nr3101.hotelbookingapp.service.impl;

import com.nr3101.hotelbookingapp.advice.ResourceNotFoundException;
import com.nr3101.hotelbookingapp.dto.request.BookingRequestDto;
import com.nr3101.hotelbookingapp.dto.request.GuestRequestDto;
import com.nr3101.hotelbookingapp.dto.response.BookingResponseDto;
import com.nr3101.hotelbookingapp.entity.*;
import com.nr3101.hotelbookingapp.entity.enums.BookingStatus;
import com.nr3101.hotelbookingapp.repository.*;
import com.nr3101.hotelbookingapp.service.BookingService;
import com.nr3101.hotelbookingapp.service.CheckoutService;
import com.nr3101.hotelbookingapp.strategy.PricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.nr3101.hotelbookingapp.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final GuestRepository guestRepository;

    private final ModelMapper modelMapper;
    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final CheckoutService checkoutService;
    private final PricingService pricingService;

    @Value("${frontend.url}")
    private String frontendUrl;


    @Override
    @Transactional
    public BookingResponseDto initializeBooking(BookingRequestDto bookingRequest) {
        log.info("Initializing booking for hotelId: {}, roomId: {}",
                bookingRequest.getHotelId(), bookingRequest.getRoomId());

        Hotel hotel = hotelRepository.findById(bookingRequest.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + bookingRequest.getHotelId()));

        Room room = roomRepository.findById(bookingRequest.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + bookingRequest.getRoomId()));

        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableInventoriesForBooking(
                room.getId(),
                bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate(),
                bookingRequest.getRoomsCount()
        );

        // One inventory row is expected for each booked date.
        long daysCount = ChronoUnit.DAYS
                .between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate()) + 1;

        if (inventoryList.size() != daysCount) {
            throw new IllegalStateException("Not enough inventory available for the selected dates");
        }

        // Reserve the rooms first so nobody else can take them while payment is pending.
        int updatedRows = inventoryRepository.initBooking(
                room.getId(),
                bookingRequest.getCheckInDate(),
                bookingRequest.getCheckOutDate(),
                bookingRequest.getRoomsCount()
        );

        if (updatedRows != daysCount) {
            throw new IllegalStateException("Not enough inventory available for the selected dates");
        }

        // Price the booking from the current inventory snapshot.
        BigDecimal singleRoomPrice = pricingService.calculateTotalPrice(inventoryList);
        BigDecimal totalPrice = singleRoomPrice.multiply(BigDecimal.valueOf(bookingRequest.getRoomsCount()));

        // Create and save booking
        Booking booking = Booking.builder()
                .hotel(hotel)
                .room(room)
                .roomsCount(bookingRequest.getRoomsCount())
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .status(BookingStatus.RESERVED)
                .user(getCurrentUser())
                .amount(totalPrice)
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        return modelMapper.map(savedBooking, BookingResponseDto.class);
    }

    @Override
    @Transactional
    public BookingResponseDto addGuestsToBooking(Long bookingId, List<GuestRequestDto> guestRequests) {
        log.info("Adding guests to booking with id: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        User user = getCurrentUser();

        // Check if the booking belongs to the current user
        if (!booking.getUser().equals(user)) {
            throw new AccessDeniedException("Booking does not belong to the current user with id: " + user.getId());
        }

        if (hasBookingExpired(booking)) {
            throw new IllegalStateException("Booking has expired. Please initialize a new booking.");
        }

        if (booking.getStatus() != BookingStatus.RESERVED) {
            throw new IllegalStateException("Guests can only be added to a booking in RESERVED status.");
        }

        guestRequests.forEach(guestRequest -> {
            Guest guest = modelMapper.map(guestRequest, Guest.class);
            guest.setUser(user);
            Guest savedGuest = guestRepository.save(guest);
            booking.getGuests().add(savedGuest);
        });


        booking.setStatus(BookingStatus.GUESTS_ADDED);
        Booking updatedBooking = bookingRepository.save(booking);
        return modelMapper.map(updatedBooking, BookingResponseDto.class);
    }

    /**
     * This method initiates the payment process for a booking by generating a Stripe checkout session URL.
     */
    @Override
    @Transactional
    public String initiatePayment(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        User user = getCurrentUser();

        // Check if the booking belongs to the current user
        if (!booking.getUser().equals(user)) {
            throw new AccessDeniedException("Booking does not belong to the current user with id: " + user.getId());
        }

        // Check if the booking has expired
        if (hasBookingExpired(booking)) {
            throw new IllegalStateException("Booking has expired. Please initialize a new booking.");
        }

        String sessionUrl = checkoutService.getCheckoutSessionUrl(
                booking,
                frontendUrl + "/payment-success",
                frontendUrl + "/payment-failure"
        );

        booking.setStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepository.save(booking);

        return sessionUrl;
    }

    /**
     * This method is called when Stripe sends a webhook event for successful payment.
     * It updates the booking status to CONFIRMED and confirms the inventory for the booking.
     */
    @Override
    @Transactional
    public void capturePaymentWebhook(Event event) {
        // We only care about the checkout.session.completed event which indicates a successful payment
        if ("checkout.session.completed".equals(event.getType())) {
            // Extract the checkout session from the event data
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session != null) {
                String sessionId = session.getId();
                log.info("Processing successful payment for Stripe session id: {}", sessionId);

                // Find the booking by payment session id first, then fall back to Stripe metadata.
                Booking booking = resolveBookingForSession(session);

                // Ignore duplicate webhook deliveries once the booking is already confirmed.
                if (booking.getStatus() == BookingStatus.CONFIRMED) {
                    log.info("Booking with id: {} already confirmed, skipping duplicate webhook", booking.getId());
                    return;
                }

                if (booking.getPaymentSessionId() == null || !sessionId.equals(booking.getPaymentSessionId())) {
                    booking.setPaymentSessionId(sessionId);
                }

                long daysCount = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate()) + 1;

                // Lock all reserved rows again before moving rooms from reserved to booked.
                List<Inventory> lockedInventories = inventoryRepository.findAndLockReservedInventoriesForBooking(
                        booking.getRoom().getId(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        booking.getRoomsCount()
                );

                if (lockedInventories.size() != daysCount) {
                    throw new IllegalStateException("Unable to lock all reserved inventories for booking id: " + booking.getId());
                }

                int confirmedRows = inventoryRepository.confirmBooking(
                        booking.getRoom().getId(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        booking.getRoomsCount()
                );

                if (confirmedRows != daysCount) {
                    throw new IllegalStateException("Failed to confirm all inventory rows for booking id: " + booking.getId());
                }

                // Mark the booking as paid only after inventory confirmation succeeds.
                booking.setStatus(BookingStatus.CONFIRMED);
                bookingRepository.save(booking);

                log.info("Booking with id: {} has been confirmed after successful payment", booking.getId());
            } else {
                log.warn("Received checkout.session.completed with no session payload");
            }
        } else {
            log.debug("Ignoring Stripe event type: {}", event.getType());
        }
    }

    /**
     * This method cancels a confirmed booking by marking it as CANCELLED, releasing the reserved inventory back to availability, and initiating a refund through Stripe.
     */
    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        User user = getCurrentUser();

        // Check if the booking belongs to the current user
        if (!booking.getUser().equals(user)) {
            throw new AccessDeniedException("Booking does not belong to the current user with id: " + user.getId());
        }

        // Check if the booking has been confirmed
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed bookings can be cancelled.");
        }

        // Mark the booking cancelled before releasing inventory.
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Lock the booked rows so cancellation cannot race with another update.
        inventoryRepository.findAndLockReservedInventoriesForBooking(
                booking.getRoom().getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getRoomsCount()
        );

        // Release the rooms back to availability.
        int cancelledRows = inventoryRepository.cancelBooking(
                booking.getRoom().getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getRoomsCount()
        );

        long daysCount = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate()) + 1;
        if (cancelledRows != daysCount) {
            throw new IllegalStateException("Failed to cancel all inventory rows for booking id: " + booking.getId());
        }

        // Handle the refund
        try {
            Session session = Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();

            Refund.create(params);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

        log.info("Booking with id: {} has been cancelled and refund initiated", booking.getId());
    }

    @Override
    public String getBookingStatus(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        User user = getCurrentUser();

        // Check if the booking belongs to the current user
        if (!booking.getUser().equals(user)) {
            throw new AccessDeniedException("Booking does not belong to the current user with id: " + user.getId());
        }

        return booking.getStatus().name();
    }

    @Override
    public List<BookingResponseDto> getBookingsForCurrentUser() {
        User user = getCurrentUser();
        log.info("Fetching bookings for user with id: {}", user.getId());

        List<Booking> bookings = bookingRepository.findByUser(user);
        return bookings.stream()
                .map(booking -> modelMapper.map(booking, BookingResponseDto.class))
                .toList();
    }


    public boolean hasBookingExpired(Booking booking) {
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    /**
     * This method resolves the booking associated with a Stripe session by first looking up the persisted payment session id, and then falling back to the booking id stored in Stripe metadata or client reference id.
     */
    private Booking resolveBookingForSession(Session session) {
        String sessionId = session.getId();

        // Prefer the persisted Stripe session id, then use the booking id stored in Stripe metadata.
        return bookingRepository.findByPaymentSessionId(sessionId)
                .or(() -> {
                    String bookingIdFromMetadata = session.getMetadata() != null
                            ? session.getMetadata().get("bookingId")
                            : null;
                    if (bookingIdFromMetadata == null) {
                        bookingIdFromMetadata = session.getClientReferenceId();
                    }
                    if (bookingIdFromMetadata == null) {
                        return java.util.Optional.empty();
                    }
                    try {
                        return bookingRepository.findById(Long.parseLong(bookingIdFromMetadata));
                    } catch (NumberFormatException ex) {
                        log.warn("Invalid booking id metadata '{}' for Stripe session {}", bookingIdFromMetadata, sessionId);
                        return java.util.Optional.empty();
                    }
                })
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found for payment session id: " + sessionId));
    }
}
