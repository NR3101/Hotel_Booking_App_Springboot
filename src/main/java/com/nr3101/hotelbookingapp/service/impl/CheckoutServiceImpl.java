package com.nr3101.hotelbookingapp.service.impl;

import com.nr3101.hotelbookingapp.entity.Booking;
import com.nr3101.hotelbookingapp.entity.User;
import com.nr3101.hotelbookingapp.repository.BookingRepository;
import com.nr3101.hotelbookingapp.service.CheckoutService;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.nr3101.hotelbookingapp.util.AppUtils.getCurrentUser;


/**
 * Service implementation for handling checkout sessions with Stripe.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutServiceImpl implements CheckoutService {

    private final BookingRepository bookingRepository;

    /**
     * Generates a Stripe checkout session URL for the given booking.
     *
     * @param booking    The booking for which to create the checkout session.
     * @param successUrl The URL to redirect to after successful payment.
     * @param failureUrl The URL to redirect to after failed payment.
     * @return The URL of the Stripe checkout session.
     */
    @Override
    public String getCheckoutSessionUrl(Booking booking, String successUrl, String failureUrl) {
        log.info("Generating checkout session URL for booking id: {}", booking.getId());
        User currentUser = getCurrentUser();

        try {
            // Create a Stripe customer for the current booking user.
            CustomerCreateParams customerCreateParams = CustomerCreateParams.builder()
                    .setEmail(currentUser.getEmail())
                    .setName(currentUser.getName())
                    .build();

            Customer customer = Customer.create(customerCreateParams);

            // Build a payment session and attach booking identity for webhook lookup.
            SessionCreateParams sessionCreateParams = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setClientReferenceId(booking.getId().toString())
                    .putMetadata("bookingId", booking.getId().toString())
                    // We need to pass BillingAddressCollection
                    .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.REQUIRED)
                    .setCustomer(customer.getId())
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(failureUrl)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("inr")
                                                    // need to pass lowest denomination of the currency, so multiplying by 100 for INR
                                                    .setUnitAmount(booking.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(booking.getHotel().getName() + " - " + booking.getRoom().getType())
                                                                    .setDescription("Booking from " + booking.getCheckInDate() + " to " + booking.getCheckOutDate() + " with booking id: " + booking.getId())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session = Session.create(sessionCreateParams);
            log.info("Checkout session created with ID: {}", session.getId());

            // Persist the Stripe session id on the booking for later webhook processing.
            booking.setPaymentSessionId(session.getId());
            bookingRepository.save(booking);

            return session.getUrl();
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }
}
