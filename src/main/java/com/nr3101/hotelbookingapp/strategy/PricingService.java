package com.nr3101.hotelbookingapp.strategy;

import com.nr3101.hotelbookingapp.entity.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * PricingService is responsible for calculating the dynamic price of a hotel room based on various factors.
 * It uses the Decorator pattern to apply multiple pricing strategies in a flexible manner.
 */
@Service
public class PricingService {

    public BigDecimal calculateDynamicPrice(Inventory inventory) {
        // Base price calculation
        PricingStrategy pricingStrategy = new BasePricingStrategy();

        // Apply surge pricing
        pricingStrategy = new SurgePricingStrategy(pricingStrategy);

        // Apply occupancy-based pricing
        pricingStrategy = new OccupancyPricingStrategy(pricingStrategy);

        // Apply urgency-based pricing
        pricingStrategy = new UrgencyPricingStrategy(pricingStrategy);

        // Apply holiday pricing
        pricingStrategy = new HolidayPricingStrategy(pricingStrategy);

        // Calculate final price using the decorated strategy
        return pricingStrategy.calculatePrice(inventory);
    }
}
