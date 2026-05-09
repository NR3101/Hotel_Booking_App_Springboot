package com.nr3101.hotelbookingapp.strategy;

import com.nr3101.hotelbookingapp.entity.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class OccupancyPricingStrategy implements PricingStrategy {

    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);

        // Apply occupancy-based pricing adjustments
        double occupancyRate = (double) inventory.getBookedCount() / inventory.getTotalCount();
        if (occupancyRate > 0.8) {
            // Increase price by 20% if occupancy is above 80%
            price = price.multiply(BigDecimal.valueOf(1.2));
        } else if (occupancyRate < 0.5) {
            // Decrease price by 10% if occupancy is below 50%
            price = price.multiply(BigDecimal.valueOf(0.9));
        }

        return price;
    }

}
