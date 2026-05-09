package com.nr3101.hotelbookingapp.service.impl;

import com.nr3101.hotelbookingapp.entity.Hotel;
import com.nr3101.hotelbookingapp.entity.HotelMinPrice;
import com.nr3101.hotelbookingapp.entity.Inventory;
import com.nr3101.hotelbookingapp.repository.HotelMinPriceRepository;
import com.nr3101.hotelbookingapp.repository.HotelRepository;
import com.nr3101.hotelbookingapp.repository.InventoryRepository;
import com.nr3101.hotelbookingapp.service.PricingUpdateService;
import com.nr3101.hotelbookingapp.strategy.PricingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PricingUpdateServiceImpl implements PricingUpdateService {

    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final PricingService pricingService;

    @Override
    @Scheduled(cron = "0 0 * * * *") // Schedule to run every hour at the top of the hour
    public void updatePrices() {
        log.info("Starting scheduled price update...");

        int page = 0;
        int batchSize = 100; // Process 100 hotels at a time

        while (true) {
            Page<Hotel> hotelPage = hotelRepository.findAll(PageRequest.of(page, batchSize));
            if (hotelPage.isEmpty()) {
                break; // No more hotels to process
            }

            hotelPage.getContent().forEach(this::updateHotelPrices);

            page++;
        }

        log.info("Completed scheduled price update.");
    }

    private void updateHotelPrices(Hotel hotel) {
        log.info("Updating prices for hotel: {}", hotel.getId());

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(1);

        List<Inventory> inventories = inventoryRepository.findByHotelAndDateBetween(hotel, startDate, endDate);

        updateInventoryPrices(inventories);
        updateHotelMinPrice(hotel, inventories, startDate, endDate);
    }

    private void updateInventoryPrices(List<Inventory> inventories) {
        log.info("Updating inventory prices for {} records", inventories.size());

        inventories.forEach(inventory -> {
            BigDecimal dynamicPrice = pricingService.calculateDynamicPrice(inventory);
            inventory.setPrice(dynamicPrice);
        });

        inventoryRepository.saveAll(inventories);
    }

    private void updateHotelMinPrice(Hotel hotel, List<Inventory> inventories, LocalDate startDate, LocalDate endDate) {
        log.info("Updating minimum prices for hotel: {}", hotel.getId());

        // Calculate the minimum price for each date
        Map<LocalDate, BigDecimal> dailyMinPrice = inventories.stream()
                .collect(Collectors.groupingBy(
                        Inventory::getDate,
                        Collectors.mapping(Inventory::getPrice, Collectors.minBy(Comparator.naturalOrder()))))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().orElse(BigDecimal.ZERO)
                ));

        // Create or update HotelMinPrice entities based on the calculated minimum prices in batch
        List<HotelMinPrice> hotelMinPrices = new ArrayList<>();
        dailyMinPrice.forEach((date, minPrice) -> {
            HotelMinPrice hotelMinPrice = hotelMinPriceRepository.findByHotelAndDate(hotel, date)
                    .orElse(new HotelMinPrice(hotel, date));
            hotelMinPrice.setMinPrice(minPrice);
            hotelMinPrices.add(hotelMinPrice);
        });

        // Save or update the minimum prices for the hotel in batch
        hotelMinPriceRepository.saveAll(hotelMinPrices);
    }
}
