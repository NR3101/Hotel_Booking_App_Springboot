package com.nr3101.hotelbookingapp.strategy;

import com.nr3101.hotelbookingapp.entity.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class HolidayPricingStrategy implements PricingStrategy {

    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);

        // Apply holiday-based pricing adjustments
        boolean isTodayHoliday = true; // We can replace this with actual holiday checking logic
        if (isTodayHoliday) {
            // Increase price by 25% on holidays
            price = price.multiply(BigDecimal.valueOf(1.25));
        }

        return price;
    }
}
