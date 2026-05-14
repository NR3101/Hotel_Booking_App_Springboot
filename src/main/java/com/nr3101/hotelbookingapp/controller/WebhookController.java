package com.nr3101.hotelbookingapp.controller;

import com.nr3101.hotelbookingapp.service.BookingService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling Stripe webhooks related to payment events.
 */
@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
@Slf4j
@Hidden
public class WebhookController {

    private final BookingService bookingService;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    /**
     * Endpoint to handle Stripe payment webhooks.
     *
     * @param payload   The raw JSON payload from Stripe.
     * @param sigHeader The Stripe signature header for verifying the webhook.
     * @return A response indicating the result of processing the webhook.
     */
    @PostMapping("/payment")
    public ResponseEntity<Void> capturePaymentWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        log.info("Received payment webhook");
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            bookingService.capturePaymentWebhook(event);

            return ResponseEntity.ok().build();
        } catch (SignatureVerificationException e) {
            log.error("Error processing payment webhook: {}", e.getMessage());
            throw new RuntimeException("Invalid webhook signature: " + e.getMessage());
        }
    }
}
