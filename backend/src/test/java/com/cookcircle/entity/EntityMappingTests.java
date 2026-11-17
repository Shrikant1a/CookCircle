package com.cookcircle.entity;

import com.cookcircle.dto.ApiResponse;
import com.cookcircle.dto.ApiError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class EntityMappingTests {

    @Test
    @DisplayName("TC-DB01: Society entity pooling fee and skip meal credit validation")
    void testSocietyEntity() {
        Society society = new Society();
        society.setCode("HG-POWAI");
        society.setName("Hiranandani Gardens");
        society.setCity("Mumbai");
        society.setBasePoolingFee(new BigDecimal("4200.00"));
        society.setSkipMealCredit(new BigDecimal("140.00"));

        assertEquals("HG-POWAI", society.getCode());
        assertEquals("Hiranandani Gardens", society.getName());
        assertEquals(new BigDecimal("4200.00"), society.getBasePoolingFee());
        assertEquals(new BigDecimal("140.00"), society.getSkipMealCredit());
        assertEquals("ACTIVE", society.getStatus());
    }

    @Test
    @DisplayName("TC-DB02: Tower and Apartment relationship and unique constraints")
    void testTowerAndApartmentEntity() {
        Tower tower = new Tower();
        tower.setName("Tower B - Wing 1");
        tower.setTotalFloors(22);

        Apartment apt = new Apartment();
        apt.setTower(tower);
        apt.setFlatNumber("402");
        apt.setFloorNumber(4);
        apt.setConfiguration("2BHK");

        assertEquals("Tower B - Wing 1", apt.getTower().getName());
        assertEquals("402", apt.getFlatNumber());
        assertEquals(4, apt.getFloorNumber());
        assertEquals("2BHK", apt.getConfiguration());
    }

    @Test
    @DisplayName("TC-DB03: Invoice net calculation with wallet discount deduction")
    void testInvoiceCalculation() {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV-2026-11-402");
        invoice.setBillingMonth("2026-11");
        invoice.setBaseAmount(new BigDecimal("4200.00"));
        invoice.setDiscountWallet(new BigDecimal("280.00"));
        invoice.setNetAmount(invoice.getBaseAmount().subtract(invoice.getDiscountWallet()));
        invoice.setDueDate(LocalDate.of(2026, 11, 5));
        invoice.setStatus("PAID");

        assertEquals(new BigDecimal("3920.00"), invoice.getNetAmount());
        assertEquals("PAID", invoice.getStatus());
    }

    @Test
    @DisplayName("TC-DB04: 45-Minute Cooking Slot timing validation")
    void testCookingSlotTiming() {
        CookingSlot slot = new CookingSlot();
        slot.setShift("EVENING");
        slot.setSlotOrder(1);
        slot.setStartTime(LocalTime.of(18, 30));
        slot.setEndTime(LocalTime.of(19, 15));
        slot.setIsActive(true);

        assertTrue(slot.getStartTime().isBefore(slot.getEndTime()));
        assertEquals(1, slot.getSlotOrder());
        assertEquals(45, java.time.Duration.between(slot.getStartTime(), slot.getEndTime()).toMinutes());
    }

    @Test
    @DisplayName("TC-DB05: Cook entity verification status and rating")
    void testCookEntity() {
        Cook cook = new Cook();
        cook.setType("PRIMARY_COOK");
        cook.setRating(new BigDecimal("4.90"));
        cook.setSpecialties("North Indian, Gujarati, Jain");
        cook.setExperienceYears(6);
        cook.setAadhaarVerified(true);
        cook.setPoliceVerified(true);
        cook.setStatus("ACTIVE");

        assertTrue(cook.getAadhaarVerified());
        assertTrue(cook.getPoliceVerified());
        assertEquals("PRIMARY_COOK", cook.getType());
        assertEquals(new BigDecimal("4.90"), cook.getRating());
    }

    @Test
    @DisplayName("TC-DB06: ApiResponse and ApiError DTO standards")
    void testApiResponseEnvelope() {
        ApiResponse<String> response = ApiResponse.ok("Booking successful", "ORDER_4829");
        assertTrue(response.isSuccess());
        assertEquals("Booking successful", response.getMessage());
        assertEquals("ORDER_4829", response.getData());
        assertNotNull(response.getTimestamp());

        ApiError error = new ApiError("SLOT_CONFLICT", "Slot is already booked", 409);
        assertEquals("SLOT_CONFLICT", error.getCode());
        assertEquals(409, error.getStatus());
    }
}

