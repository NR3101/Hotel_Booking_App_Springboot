package com.nr3101.hotelbookingapp.strategy;

import com.nr3101.hotelbookingapp.entity.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@RequiredArgsConstructor
public class UrgencyPricingStrategy implements PricingStrategy {

    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);

        // Apply urgency-based pricing adjustments
        LocalDate today = LocalDate.now();
        if (!inventory.getDate().isBefore(today) && inventory.getDate().isBefore(today.plusDays(7))) {
            // Increase price by 20% if the booking date is within the next 7 days
            price = price.multiply(BigDecimal.valueOf(1.2));
        }

        return price;
    }

}
