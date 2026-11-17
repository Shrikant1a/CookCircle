package com.cookcircle.service;

import com.cookcircle.dto.booking.BookingDto;
import com.cookcircle.dto.booking.CreateBookingRequest;
import com.cookcircle.dto.slot.CookingSlotDto;
import com.cookcircle.dto.slot.SlotAvailabilityDto;
import com.cookcircle.entity.*;
import com.cookcircle.exception.SlotConflictException;
import com.cookcircle.repository.*;
import com.cookcircle.service.impl.BookingServiceImpl;
import com.cookcircle.service.impl.CookingSlotServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class BookingSystemTests {

    private final Map<Long, Booking> bookingTable = new HashMap<>();
    private final Map<Long, CookingSlot> slotTable = new HashMap<>();
    private final Map<Long, Resident> residentTable = new HashMap<>();
    private final Map<Long, Apartment> apartmentTable = new HashMap<>();
    private final Map<Long, Tower> towerTable = new HashMap<>();
    private final Map<Long, Society> societyTable = new HashMap<>();
    private final Map<Long, Cook> cookTable = new HashMap<>();
    private final Map<Long, CookAssignment> assignmentTable = new HashMap<>();
    private final Map<Long, User> userTable = new HashMap<>();

    private BookingRepository bookingRepository;
    private CookingSlotRepository cookingSlotRepository;
    private ResidentRepository residentRepository;
    private CookRepository cookRepository;
    private CookAssignmentRepository cookAssignmentRepository;
    private TowerRepository towerRepository;

    private CookingSlotService cookingSlotService;
    private BookingService bookingService;

    @SuppressWarnings("unchecked")
    private <T> T createProxy(Class<T> intf, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(intf.getClassLoader(), new Class<?>[]{intf}, handler);
    }

    @BeforeEach
    void setUp() {
        bookingTable.clear();
        slotTable.clear();
        residentTable.clear();
        apartmentTable.clear();
        towerTable.clear();
        societyTable.clear();
        cookTable.clear();
        assignmentTable.clear();
        userTable.clear();

        bookingRepository = createProxy(BookingRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("save".equals(name)) {
                Booking b = (Booking) args[0];
                if (b.getId() == null) {
                    b.setId((long) (bookingTable.size() + 1));
                }
                bookingTable.put(b.getId(), b);
                return b;
            } else if ("findById".equals(name)) {
                Long id = (Long) args[0];
                return Optional.ofNullable(bookingTable.get(id));
            } else if ("existsBySlotIdAndBookingDateAndStatusIn".equals(name)) {
                Long sId = (Long) args[0];
                LocalDate d = (LocalDate) args[1];
                List<String> statuses = (List<String>) args[2];
                return bookingTable.values().stream().anyMatch(b ->
                        b.getSlot().getId().equals(sId)
                                && b.getBookingDate().equals(d)
                                && statuses.contains(b.getStatus()));
            } else if ("existsByApartmentIdAndBookingDateAndSlotShiftAndStatusIn".equals(name)) {
                Long aptId = (Long) args[0];
                LocalDate d = (LocalDate) args[1];
                String shift = (String) args[2];
                List<String> statuses = (List<String>) args[3];
                return bookingTable.values().stream().anyMatch(b ->
                        b.getApartment().getId().equals(aptId)
                                && b.getBookingDate().equals(d)
                                && b.getSlot().getShift().equalsIgnoreCase(shift)
                                && statuses.contains(b.getStatus()));
            } else if ("findBySlotIdAndBookingDateAndStatusIn".equals(name)) {
                Long sId = (Long) args[0];
                LocalDate d = (LocalDate) args[1];
                List<String> statuses = (List<String>) args[2];
                return bookingTable.values().stream().filter(b ->
                        b.getSlot().getId().equals(sId)
                                && b.getBookingDate().equals(d)
                                && statuses.contains(b.getStatus())).findFirst();
            } else if ("findByResidentIdAndBookingDateBetweenOrderByBookingDateAsc".equals(name)) {
                Long rId = (Long) args[0];
                LocalDate start = (LocalDate) args[1];
                LocalDate end = (LocalDate) args[2];
                return bookingTable.values().stream().filter(b ->
                        b.getResident().getId().equals(rId)
                                && !b.getBookingDate().isBefore(start)
                                && !b.getBookingDate().isAfter(end)).toList();
            } else if ("findByEffectiveCookIdAndBookingDate".equals(name)) {
                Long cId = (Long) args[0];
                LocalDate d = (LocalDate) args[1];
                return bookingTable.values().stream().filter(b ->
                        b.getEffectiveCook().getId().equals(cId)
                                && b.getBookingDate().equals(d)).toList();
            } else if ("findBySlotTowerIdAndBookingDate".equals(name)) {
                Long tId = (Long) args[0];
                LocalDate d = (LocalDate) args[1];
                return bookingTable.values().stream().filter(b ->
                        b.getSlot().getTower().getId().equals(tId)
                                && b.getBookingDate().equals(d)).toList();
            }
            return null;
        });

        cookingSlotRepository = createProxy(CookingSlotRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("save".equals(name)) {
                CookingSlot s = (CookingSlot) args[0];
                if (s.getId() == null) {
                    s.setId((long) (slotTable.size() + 1));
                }
                slotTable.put(s.getId(), s);
                return s;
            } else if ("findById".equals(name)) {
                Long id = (Long) args[0];
                return Optional.ofNullable(slotTable.get(id));
            } else if ("findByTowerId".equals(name)) {
                Long tId = (Long) args[0];
                return slotTable.values().stream().filter(s -> s.getTower().getId().equals(tId)).toList();
            } else if ("findByTowerIdOrderByShiftAscSlotOrderAsc".equals(name)) {
                Long tId = (Long) args[0];
                return slotTable.values().stream()
                        .filter(s -> s.getTower().getId().equals(tId))
                        .sorted(Comparator.comparing(CookingSlot::getShift).thenComparing(CookingSlot::getSlotOrder))
                        .toList();
            } else if ("findByTowerIdAndShiftOrderBySlotOrderAsc".equals(name)) {
                Long tId = (Long) args[0];
                String shift = (String) args[1];
                return slotTable.values().stream()
                        .filter(s -> s.getTower().getId().equals(tId) && s.getShift().equalsIgnoreCase(shift))
                        .sorted(Comparator.comparing(CookingSlot::getSlotOrder))
                        .toList();
            } else if ("existsByTowerIdAndShiftAndSlotOrder".equals(name)) {
                Long tId = (Long) args[0];
                String shift = (String) args[1];
                Integer order = (Integer) args[2];
                return slotTable.values().stream().anyMatch(s ->
                        s.getTower().getId().equals(tId)
                                && s.getShift().equalsIgnoreCase(shift)
                                && s.getSlotOrder().equals(order));
            }
            return null;
        });

        residentRepository = createProxy(ResidentRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("findByUserId".equals(name)) {
                Long uId = (Long) args[0];
                return residentTable.values().stream().filter(r -> r.getUser().getId().equals(uId)).findFirst();
            } else if ("save".equals(name)) {
                Resident r = (Resident) args[0];
                residentTable.put(r.getId(), r);
                return r;
            }
            return null;
        });

        cookRepository = createProxy(CookRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("findByUserId".equals(name)) {
                Long uId = (Long) args[0];
                return cookTable.values().stream().filter(c -> c.getUser().getId().equals(uId)).findFirst();
            } else if ("findById".equals(name)) {
                Long id = (Long) args[0];
                return Optional.ofNullable(cookTable.get(id));
            }
            return null;
        });

        cookAssignmentRepository = createProxy(CookAssignmentRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("findByTowerIdAndStatus".equals(name)) {
                Long tId = (Long) args[0];
                String st = (String) args[1];
                return assignmentTable.values().stream()
                        .filter(a -> a.getTower().getId().equals(tId) && a.getStatus().equalsIgnoreCase(st))
                        .toList();
            }
            return null;
        });

        towerRepository = createProxy(TowerRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("findById".equals(name)) {
                Long id = (Long) args[0];
                return Optional.ofNullable(towerTable.get(id));
            } else if ("existsById".equals(name)) {
                Long id = (Long) args[0];
                return towerTable.containsKey(id);
            }
            return null;
        });

        cookingSlotService = new CookingSlotServiceImpl(
                cookingSlotRepository,
                towerRepository,
                bookingRepository,
                cookAssignmentRepository
        );

        bookingService = new BookingServiceImpl(
                bookingRepository,
                cookingSlotRepository,
                residentRepository,
                cookRepository,
                cookAssignmentRepository,
                towerRepository
        );

        // Seed Society & Tower
        Society society = new Society();
        society.setId(1L);
        society.setName("Hiranandani Gardens");
        society.setSkipMealCredit(new BigDecimal("140.00"));
        societyTable.put(1L, society);

        Tower tower = new Tower();
        tower.setId(10L);
        tower.setName("Tower A");
        tower.setSociety(society);
        towerTable.put(10L, tower);

        // Seed Cook
        User cookUser = new User();
        cookUser.setId(301L);
        cookUser.setFullName("Sunita Devi");
        cookUser.setEmail("sunita@cookcircle.com");
        cookUser.setPhone("+91 98200 11223");
        userTable.put(301L, cookUser);

        Cook cook = new Cook();
        cook.setId(101L);
        cook.setUser(cookUser);
        cook.setSociety(society);
        cook.setType("PRIMARY_COOK");
        cook.setStatus("ACTIVE");
        cookTable.put(101L, cook);

        // Assign Cook to Tower for BOTH shifts
        CookAssignment assignment = new CookAssignment();
        assignment.setId(501L);
        assignment.setCook(cook);
        assignment.setTower(tower);
        assignment.setShift("BOTH");
        assignment.setStatus("ACTIVE");
        assignmentTable.put(501L, assignment);

        // Seed Resident & Flat
        Apartment apt = new Apartment();
        apt.setId(201L);
        apt.setTower(tower);
        apt.setFlatNumber("Flat 302");
        apt.setFloorNumber(3);
        apt.setConfiguration("2BHK");
        apartmentTable.put(201L, apt);

        User resUser = new User();
        resUser.setId(401L);
        resUser.setFullName("Aarav Sharma");
        resUser.setEmail("aarav.sharma@example.com");
        resUser.setPhone("+91 98111 22334");
        userTable.put(401L, resUser);

        Resident resident = new Resident();
        resident.setId(601L);
        resident.setUser(resUser);
        resident.setSociety(society);
        resident.setApartment(apt);
        resident.setWalletBalance(BigDecimal.ZERO);
        residentTable.put(601L, resident);
    }

    @Test
    @DisplayName("TC-01: Auto-generate standard 5-morning and 5-evening 45-min slots for a tower")
    void testInitializeDefaultSlots() {
        cookingSlotService.initializeDefaultSlotsForTower(10L);

        List<CookingSlotDto> morningSlots = cookingSlotService.getSlotsByTower(10L, "MORNING");
        assertEquals(5, morningSlots.size());
        assertEquals(LocalTime.of(7, 0), morningSlots.get(0).getStartTime());
        assertEquals(LocalTime.of(7, 45), morningSlots.get(0).getEndTime());
        assertEquals(LocalTime.of(10, 0), morningSlots.get(4).getStartTime());
        assertEquals(LocalTime.of(10, 45), morningSlots.get(4).getEndTime());

        List<CookingSlotDto> eveningSlots = cookingSlotService.getSlotsByTower(10L, "EVENING");
        assertEquals(5, eveningSlots.size());
        assertEquals(LocalTime.of(18, 0), eveningSlots.get(0).getStartTime());
        assertEquals(LocalTime.of(18, 45), eveningSlots.get(0).getEndTime());
        assertEquals(LocalTime.of(21, 0), eveningSlots.get(4).getStartTime());
        assertEquals(LocalTime.of(21, 45), eveningSlots.get(4).getEndTime());
    }

    @Test
    @DisplayName("TC-02: Successfully book a 45-min slot with 4-digit arrival OTP")
    void testCreateBookingSuccess() {
        cookingSlotService.initializeDefaultSlotsForTower(10L);
        List<CookingSlotDto> morningSlots = cookingSlotService.getSlotsByTower(10L, "MORNING");
        CookingSlotDto slot1 = morningSlots.get(0);

        LocalDate bookingDate = LocalDate.now().plusDays(1);
        CreateBookingRequest req = new CreateBookingRequest(slot1.getId(), bookingDate, "Dal Tadka, Jeera Rice, Rotis");

        BookingDto booking = bookingService.createBooking(401L, req);

        assertNotNull(booking);
        assertEquals(1L, booking.getId());
        assertEquals("Flat 302", booking.getFlatNumber());
        assertEquals("Aarav Sharma", booking.getResidentName());
        assertEquals("Sunita Devi", booking.getPrimaryCookName());
        assertEquals("Sunita Devi", booking.getEffectiveCookName());
        assertEquals(bookingDate, booking.getBookingDate());
        assertEquals("CONFIRMED", booking.getStatus());
        assertNotNull(booking.getArrivalOtp());
        assertEquals(4, booking.getArrivalOtp().length());
    }

    @Test
    @DisplayName("TC-03: Prevent double booking of the same slot on the same date")
    void testPreventDoubleBookingSameSlot() {
        cookingSlotService.initializeDefaultSlotsForTower(10L);
        List<CookingSlotDto> morningSlots = cookingSlotService.getSlotsByTower(10L, "MORNING");
        CookingSlotDto slot1 = morningSlots.get(0);

        LocalDate bookingDate = LocalDate.now().plusDays(1);
        CreateBookingRequest req1 = new CreateBookingRequest(slot1.getId(), bookingDate, "Menu 1");
        bookingService.createBooking(401L, req1);

        // Another resident in another flat trying to book the exact same slot
        Apartment apt2 = new Apartment();
        apt2.setId(202L);
        apt2.setTower(towerTable.get(10L));
        apt2.setFlatNumber("Flat 405");
        apartmentTable.put(202L, apt2);

        User resUser2 = new User();
        resUser2.setId(402L);
        resUser2.setFullName("Priya Patel");
        userTable.put(402L, resUser2);

        Resident res2 = new Resident();
        res2.setId(602L);
        res2.setUser(resUser2);
        res2.setSociety(societyTable.get(1L));
        res2.setApartment(apt2);
        res2.setWalletBalance(BigDecimal.ZERO);
        residentTable.put(602L, res2);

        CreateBookingRequest req2 = new CreateBookingRequest(slot1.getId(), bookingDate, "Menu 2");
        assertThrows(SlotConflictException.class, () -> bookingService.createBooking(402L, req2));
    }

    @Test
    @DisplayName("TC-04: Prevent same flat from booking two slots in the same shift on the same date")
    void testPreventApartmentShiftDoubleBooking() {
        cookingSlotService.initializeDefaultSlotsForTower(10L);
        List<CookingSlotDto> morningSlots = cookingSlotService.getSlotsByTower(10L, "MORNING");
        CookingSlotDto slot1 = morningSlots.get(0);
        CookingSlotDto slot2 = morningSlots.get(1);

        LocalDate bookingDate = LocalDate.now().plusDays(1);
        CreateBookingRequest req1 = new CreateBookingRequest(slot1.getId(), bookingDate, "Menu 1");
        bookingService.createBooking(401L, req1);

        // Same resident tries to book Slot 2 in the same Morning shift
        CreateBookingRequest req2 = new CreateBookingRequest(slot2.getId(), bookingDate, "Menu 2");
        assertThrows(SlotConflictException.class, () -> bookingService.createBooking(401L, req2));
    }

    @Test
    @DisplayName("TC-05: Cook enters 4-digit OTP to start session and completes cooking")
    void testCookOtpAndCompletion() {
        cookingSlotService.initializeDefaultSlotsForTower(10L);
        CookingSlotDto slot = cookingSlotService.getSlotsByTower(10L, "MORNING").get(0);
        LocalDate bookingDate = LocalDate.now().plusDays(1);

        BookingDto booking = bookingService.createBooking(401L, new CreateBookingRequest(slot.getId(), bookingDate, "Paneer Sabzi"));
        String correctOtp = booking.getArrivalOtp();

        // Wrong OTP fails
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.startCookingWithOtp(301L, booking.getId(), "0000");
        });

        // Correct OTP starts session
        BookingDto inProgress = bookingService.startCookingWithOtp(301L, booking.getId(), correctOtp);
        assertEquals("COOKING", inProgress.getStatus());

        // Cook completes slot
        BookingDto completed = bookingService.completeBooking(301L, booking.getId());
        assertEquals("COMPLETED", completed.getStatus());
    }

    @Test
    @DisplayName("TC-06: Resident skips meal and receives +₹140 wallet credit")
    void testSkipMealAndWalletCredit() {
        cookingSlotService.initializeDefaultSlotsForTower(10L);
        CookingSlotDto slot = cookingSlotService.getSlotsByTower(10L, "MORNING").get(0);
        LocalDate bookingDate = LocalDate.now().plusDays(2);

        BookingDto booking = bookingService.createBooking(401L, new CreateBookingRequest(slot.getId(), bookingDate, "Meal to skip"));
        assertEquals("CONFIRMED", booking.getStatus());

        Resident resBefore = residentTable.get(601L);
        assertEquals(BigDecimal.ZERO, resBefore.getWalletBalance());

        // Skip meal
        BookingDto skipped = bookingService.skipMeal(401L, booking.getId());
        assertEquals("SKIPPED", skipped.getStatus());
        assertEquals(new BigDecimal("140.00"), skipped.getSkipRefundAmount());

        Resident resAfter = residentTable.get(601L);
        assertEquals(new BigDecimal("140.00"), resAfter.getWalletBalance());
    }

    @Test
    @DisplayName("TC-07: Slot availability grid reflects booked vs free slots")
    void testSlotAvailabilityGrid() {
        cookingSlotService.initializeDefaultSlotsForTower(10L);
        CookingSlotDto slot1 = cookingSlotService.getSlotsByTower(10L, "MORNING").get(0);
        LocalDate targetDate = LocalDate.now().plusDays(3);

        // Before booking: all available
        List<SlotAvailabilityDto> gridBefore = cookingSlotService.getAvailableSlotsForDate(10L, targetDate, "MORNING");
        assertTrue(gridBefore.get(0).isAvailable());
        assertEquals("AVAILABLE", gridBefore.get(0).getBookingStatus());

        // Book slot 1
        bookingService.createBooking(401L, new CreateBookingRequest(slot1.getId(), targetDate, "Breakfast"));

        // After booking: slot 1 is not available, booked by Flat 302
        List<SlotAvailabilityDto> gridAfter = cookingSlotService.getAvailableSlotsForDate(10L, targetDate, "MORNING");
        assertFalse(gridAfter.get(0).isAvailable());
        assertEquals("Flat 302", gridAfter.get(0).getBookedFlatNumber());
        assertEquals("CONFIRMED", gridAfter.get(0).getBookingStatus());

        // Slot 2 remains free
        assertTrue(gridAfter.get(1).isAvailable());
        assertEquals("AVAILABLE", gridAfter.get(1).getBookingStatus());
    }
}

