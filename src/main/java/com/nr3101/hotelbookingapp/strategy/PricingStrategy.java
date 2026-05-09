package com.nr3101.hotelbookingapp.strategy;

import com.nr3101.hotelbookingapp.entity.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculatePrice(Inventory inventory);
}
