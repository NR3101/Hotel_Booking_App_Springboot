package com.nr3101.hotelbookingapp.config;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Stripe API integration.
 * It sets the Stripe API key from application properties.
 */
@Configuration
public class StripeConfig {

    public StripeConfig(@Value("${stripe.secret.key}") String secretKey) {
        Stripe.apiKey = secretKey;
    }
}
