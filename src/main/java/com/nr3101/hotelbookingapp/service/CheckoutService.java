package com.nr3101.hotelbookingapp.service;

import com.nr3101.hotelbookingapp.entity.Booking;

public interface CheckoutService {

    String getCheckoutSessionUrl(Booking booking, String successUrl, String failureUrl);
}
