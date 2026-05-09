package com.nr3101.hotelbookingapp.strategy;

import com.nr3101.hotelbookingapp.entity.Inventory;
import org.springframework.context.annotation.Primary;

import java.math.BigDecimal;

@Primary // Marks this as the default implementation of PricingStrategy for dependency injection
public class BasePricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        return inventory.getRoom().getBasePrice();
    }
}
