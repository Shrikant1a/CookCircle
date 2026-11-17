package com.cookcircle.service;

import com.cookcircle.dto.attendance.AttendanceDto;
import com.cookcircle.dto.attendance.LogAttendanceRequest;
import com.cookcircle.dto.leave.ApplyLeaveRequest;
import com.cookcircle.dto.leave.BackupAssignmentDto;
import com.cookcircle.dto.leave.LeaveRequestDto;
import com.cookcircle.dto.leave.ReviewLeaveRequest;
import com.cookcircle.entity.*;
import com.cookcircle.repository.*;
import com.cookcircle.service.impl.AttendanceServiceImpl;
import com.cookcircle.service.impl.LeaveAndBackupServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class AttendanceAndBackupTests {

    private final Map<Long, Attendance> attendanceTable = new HashMap<>();
    private final Map<Long, LeaveRequest> leaveTable = new HashMap<>();
    private final Map<Long, BackupAssignment> backupTable = new HashMap<>();
    private final Map<Long, Booking> bookingTable = new HashMap<>();
    private final Map<Long, Cook> cookTable = new HashMap<>();
    private final Map<Long, User> userTable = new HashMap<>();
    private final Map<Long, Society> societyTable = new HashMap<>();
    private final Map<Long, Tower> towerTable = new HashMap<>();
    private final Map<Long, CookingSlot> slotTable = new HashMap<>();
    private final Map<Long, Apartment> apartmentTable = new HashMap<>();

    private AttendanceRepository attendanceRepository;
    private LeaveRequestRepository leaveRequestRepository;
    private BackupAssignmentRepository backupAssignmentRepository;
    private BookingRepository bookingRepository;
    private CookRepository cookRepository;
    private UserRepository userRepository;
    private SocietyRepository societyRepository;

    private AttendanceService attendanceService;
    private LeaveAndBackupService leaveAndBackupService;

    @SuppressWarnings("unchecked")
    private <T> T createProxy(Class<T> intf, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(intf.getClassLoader(), new Class<?>[]{intf}, handler);
    }

    @BeforeEach
    void setUp() {
        attendanceTable.clear();
        leaveTable.clear();
        backupTable.clear();
        bookingTable.clear();
        cookTable.clear();
        userTable.clear();
        societyTable.clear();
        towerTable.clear();
        slotTable.clear();
        apartmentTable.clear();

        attendanceRepository = createProxy(AttendanceRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("save".equals(name)) {
                Attendance a = (Attendance) args[0];
                if (a.getId() == null) {
                    a.setId((long) (attendanceTable.size() + 1));
                }
                attendanceTable.put(a.getId(), a);
                return a;
            } else if ("findByBookingIdAndCookId".equals(name)) {
                Long bId = (Long) args[0];
                Long cId = (Long) args[1];
                return attendanceTable.values().stream()
                        .filter(a -> a.getBooking().getId().equals(bId) && a.getCook().getId().equals(cId))
                        .findFirst();
            } else if ("findByCookIdAndAttendanceDateBetween".equals(name)) {
                Long cId = (Long) args[0];
                LocalDate start = (LocalDate) args[1];
                LocalDate end = (LocalDate) args[2];
                return attendanceTable.values().stream()
                        .filter(a -> a.getCook().getId().equals(cId)
                                && !a.getAttendanceDate().isBefore(start)
                                && !a.getAttendanceDate().isAfter(end))
                        .toList();
            } else if ("findByCookIdAndAttendanceDate".equals(name)) {
                Long cId = (Long) args[0];
                LocalDate d = (LocalDate) args[1];
                return attendanceTable.values().stream()
                        .filter(a -> a.getCook().getId().equals(cId) && a.getAttendanceDate().equals(d))
                        .toList();
            }
            return null;
        });

        leaveRequestRepository = createProxy(LeaveRequestRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("save".equals(name)) {
                LeaveRequest l = (LeaveRequest) args[0];
                if (l.getId() == null) {
                    l.setId((long) (leaveTable.size() + 1));
                }
                leaveTable.put(l.getId(), l);
                return l;
            } else if ("findById".equals(name)) {
                Long id = (Long) args[0];
                return Optional.ofNullable(leaveTable.get(id));
            } else if ("findByCookId".equals(name)) {
                Long cId = (Long) args[0];
                return leaveTable.values().stream().filter(l -> l.getCook().getId().equals(cId)).toList();
            } else if ("findByCookSocietyIdAndStatus".equals(name)) {
                Long sId = (Long) args[0];
                String st = (String) args[1];
                return leaveTable.values().stream()
                        .filter(l -> l.getCook().getSociety().getId().equals(sId) && l.getStatus().equalsIgnoreCase(st))
                        .toList();
            } else if ("existsByCookIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual".equals(name)) {
                Long cId = (Long) args[0];
                String st = (String) args[1];
                LocalDate end = (LocalDate) args[2];
                LocalDate start = (LocalDate) args[3];
                return leaveTable.values().stream().anyMatch(l ->
                        l.getCook().getId().equals(cId)
                                && l.getStatus().equalsIgnoreCase(st)
                                && !l.getStartDate().isAfter(end)
                                && !l.getEndDate().isBefore(start));
            }
            return null;
        });

        backupAssignmentRepository = createProxy(BackupAssignmentRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("save".equals(name)) {
                BackupAssignment b = (BackupAssignment) args[0];
                if (b.getId() == null) {
                    b.setId((long) (backupTable.size() + 1));
                }
                backupTable.put(b.getId(), b);
                return b;
            } else if ("findByLeaveRequestId".equals(name)) {
                Long lId = (Long) args[0];
                return backupTable.values().stream().filter(b -> b.getLeaveRequest().getId().equals(lId)).toList();
            } else if ("findByBackupCookIdAndAssignmentDate".equals(name)) {
                Long cId = (Long) args[0];
                LocalDate d = (LocalDate) args[1];
                return backupTable.values().stream()
                        .filter(b -> b.getBackupCook().getId().equals(cId) && b.getAssignmentDate().equals(d))
                        .toList();
            }
            return null;
        });

        bookingRepository = createProxy(BookingRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("findById".equals(name)) {
                Long id = (Long) args[0];
                return Optional.ofNullable(bookingTable.get(id));
            } else if ("save".equals(name)) {
                Booking b = (Booking) args[0];
                bookingTable.put(b.getId(), b);
                return b;
            } else if ("findByPrimaryCookIdAndBookingDate".equals(name)) {
                Long cId = (Long) args[0];
                LocalDate d = (LocalDate) args[1];
                return bookingTable.values().stream()
                        .filter(b -> b.getPrimaryCook().getId().equals(cId) && b.getBookingDate().equals(d))
                        .toList();
            }
            return null;
        });

        cookRepository = createProxy(CookRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("findById".equals(name)) {
                Long id = (Long) args[0];
                return Optional.ofNullable(cookTable.get(id));
            } else if ("findByUserId".equals(name)) {
                Long uId = (Long) args[0];
                return cookTable.values().stream().filter(c -> c.getUser().getId().equals(uId)).findFirst();
            } else if ("findBySocietyId".equals(name)) {
                Long sId = (Long) args[0];
                return cookTable.values().stream().filter(c -> c.getSociety().getId().equals(sId)).toList();
            } else if ("findBySocietyIdAndTypeAndStatus".equals(name)) {
                Long sId = (Long) args[0];
                String type = (String) args[1];
                String st = (String) args[2];
                return cookTable.values().stream()
                        .filter(c -> c.getSociety().getId().equals(sId)
                                && c.getType().equalsIgnoreCase(type)
                                && c.getStatus().equalsIgnoreCase(st))
                        .toList();
            } else if ("findBySocietyIdAndStatus".equals(name)) {
                Long sId = (Long) args[0];
                String st = (String) args[1];
                return cookTable.values().stream()
                        .filter(c -> c.getSociety().getId().equals(sId) && c.getStatus().equalsIgnoreCase(st))
                        .toList();
            } else if ("save".equals(name)) {
                Cook c = (Cook) args[0];
                cookTable.put(c.getId(), c);
                return c;
            }
            return null;
        });

        userRepository = createProxy(UserRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("findById".equals(name)) {
                Long id = (Long) args[0];
                return Optional.ofNullable(userTable.get(id));
            }
            return null;
        });

        societyRepository = createProxy(SocietyRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("existsById".equals(name)) {
                Long id = (Long) args[0];
                return societyTable.containsKey(id);
            }
            return null;
        });

        attendanceService = new AttendanceServiceImpl(attendanceRepository, bookingRepository, cookRepository);
        leaveAndBackupService = new LeaveAndBackupServiceImpl(
                leaveRequestRepository,
                backupAssignmentRepository,
                cookRepository,
                bookingRepository,
                userRepository,
                societyRepository
        );

        // Seed Society & Tower
        Society society = new Society();
        society.setId(1L);
        society.setName("Hiranandani Gardens");
        societyTable.put(1L, society);

        Tower tower = new Tower();
        tower.setId(10L);
        tower.setName("Tower A");
        tower.setSociety(society);
        towerTable.put(10L, tower);

        // Seed Admin User
        User admin = new User();
        admin.setId(10L);
        admin.setFullName("Society Administrator");
        userTable.put(10L, admin);

        // Seed Primary Cook (Sunita Devi)
        User sunitaUser = new User();
        sunitaUser.setId(101L);
        sunitaUser.setFullName("Sunita Devi");
        userTable.put(101L, sunitaUser);

        Cook sunita = new Cook();
        sunita.setId(201L);
        sunita.setUser(sunitaUser);
        sunita.setSociety(society);
        sunita.setType("PRIMARY_COOK");
        sunita.setStatus("ACTIVE");
        cookTable.put(201L, sunita);

        // Seed Standby Float Cook (Ramesh Kumar)
        User rameshUser = new User();
        rameshUser.setId(102L);
        rameshUser.setFullName("Ramesh Kumar");
        userTable.put(102L, rameshUser);

        Cook ramesh = new Cook();
        ramesh.setId(202L);
        ramesh.setUser(rameshUser);
        ramesh.setSociety(society);
        ramesh.setType("BACKUP_FLOAT_COOK");
        ramesh.setStatus("ACTIVE");
        cookTable.put(202L, ramesh);

        // Seed Apartment & Slot
        Apartment apt = new Apartment();
        apt.setId(501L);
        apt.setTower(tower);
        apt.setFlatNumber("Flat 302");
        apartmentTable.put(501L, apt);

        CookingSlot slot = new CookingSlot();
        slot.setId(601L);
        slot.setTower(tower);
        slot.setShift("MORNING");
        slot.setSlotOrder(1);
        slotTable.put(601L, slot);

        // Seed Confirmed Booking for Sunita Devi tomorrow
        Booking booking = new Booking();
        booking.setId(701L);
        booking.setSociety(society);
        booking.setSlot(slot);
        booking.setApartment(apt);
        booking.setPrimaryCook(sunita);
        booking.setEffectiveCook(sunita);
        booking.setBookingDate(LocalDate.now().plusDays(1));
        booking.setStatus("CONFIRMED");
        bookingTable.put(701L, booking);
    }

    @Test
    @DisplayName("TC-01: Cook successfully applies for leave")
    void testApplyLeaveSuccess() {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(2);
        ApplyLeaveRequest req = new ApplyLeaveRequest(start, end, "Family wedding out of town");

        LeaveRequestDto leave = leaveAndBackupService.applyLeave(101L, req);

        assertNotNull(leave);
        assertEquals(1L, leave.getId());
        assertEquals("Sunita Devi", leave.getCookName());
        assertEquals("PENDING", leave.getStatus());
        assertEquals(start, leave.getStartDate());
        assertEquals(end, leave.getEndDate());
        assertEquals("Family wedding out of town", leave.getReason());
    }

    @Test
    @DisplayName("TC-02: Prevent applying for overlapping approved leave")
    void testPreventOverlappingApprovedLeave() {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(3);

        LeaveRequest existing = new LeaveRequest();
        existing.setId(99L);
        existing.setCook(cookTable.get(201L));
        existing.setStartDate(start);
        existing.setEndDate(end);
        existing.setStatus("APPROVED");
        leaveTable.put(99L, existing);

        ApplyLeaveRequest overlappingReq = new ApplyLeaveRequest(start.plusDays(1), end.plusDays(1), "Overlapping trip");
        assertThrows(IllegalArgumentException.class, () -> leaveAndBackupService.applyLeave(101L, overlappingReq));
    }

    @Test
    @DisplayName("TC-03: Leave approval triggers auto standby backup float cook dispatch with zero disruption")
    void testApproveLeaveAutoBackupDispatch() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        ApplyLeaveRequest req = new ApplyLeaveRequest(tomorrow, tomorrow, "Urgent doctor visit");
        LeaveRequestDto applied = leaveAndBackupService.applyLeave(101L, req);

        // Verify booking has Sunita as effective cook prior to leave approval
        Booking beforeApproval = bookingTable.get(701L);
        assertEquals("Sunita Devi", beforeApproval.getEffectiveCook().getUser().getFullName());

        // Admin approves leave
        ReviewLeaveRequest review = new ReviewLeaveRequest(null, "Approved leave. Auto-dispatch standby cook.");
        LeaveRequestDto approved = leaveAndBackupService.approveLeave(10L, applied.getId(), review);

        assertEquals("APPROVED", approved.getStatus());
        assertEquals("Society Administrator", approved.getActionByName());
        assertEquals("Ramesh Kumar", approved.getBackupCookName());
        assertEquals(1, approved.getAffectedBookingsCount());

        // Primary cook is now ON_LEAVE
        assertEquals("ON_LEAVE", cookTable.get(201L).getStatus());

        // Verify booking effective cook is updated to Ramesh Kumar (Zero disruption!)
        Booking afterApproval = bookingTable.get(701L);
        assertEquals("Sunita Devi", afterApproval.getPrimaryCook().getUser().getFullName());
        assertEquals("Ramesh Kumar", afterApproval.getEffectiveCook().getUser().getFullName());

        // Verify BackupAssignment record created
        assertEquals(1, backupTable.size());
        BackupAssignment ba = backupTable.values().iterator().next();
        assertEquals("Ramesh Kumar", ba.getBackupCook().getUser().getFullName());
        assertEquals("Tower A", ba.getTower().getName());
        assertEquals(tomorrow, ba.getAssignmentDate());
    }

    @Test
    @DisplayName("TC-04: Admin rejects cook leave request")
    void testRejectLeave() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        ApplyLeaveRequest req = new ApplyLeaveRequest(tomorrow, tomorrow, "Personal work");
        LeaveRequestDto applied = leaveAndBackupService.applyLeave(101L, req);

        ReviewLeaveRequest review = new ReviewLeaveRequest(null, "Cannot approve due to high festival booking volume");
        LeaveRequestDto rejected = leaveAndBackupService.rejectLeave(10L, applied.getId(), review);

        assertEquals("REJECTED", rejected.getStatus());
        assertEquals("Cannot approve due to high festival booking volume", rejected.getActionNotes());

        // Cook status remains ACTIVE
        assertEquals("ACTIVE", cookTable.get(201L).getStatus());

        // Booking effective cook remains Sunita Devi
        Booking booking = bookingTable.get(701L);
        assertEquals("Sunita Devi", booking.getEffectiveCook().getUser().getFullName());
    }

    @Test
    @DisplayName("TC-05: Cook check-in and check-out attendance logging")
    void testAttendanceLogging() {
        LogAttendanceRequest checkInReq = new LogAttendanceRequest(701L, "ON_TIME", "Arrived on schedule");
        AttendanceDto checkIn = attendanceService.logAttendance(101L, checkInReq);

        assertNotNull(checkIn);
        assertEquals(1L, checkIn.getId());
        assertEquals("Sunita Devi", checkIn.getCookName());
        assertEquals("Flat 302", checkIn.getFlatNumber());
        assertEquals("ON_TIME", checkIn.getStatus());
        assertNotNull(checkIn.getCheckInTime());
        assertNull(checkIn.getCheckOutTime());

        // Log check-out on completion
        LogAttendanceRequest checkOutReq = new LogAttendanceRequest(701L, "ON_TIME", "Session completed cleanly");
        AttendanceDto checkOut = attendanceService.logAttendance(101L, checkOutReq);

        assertNotNull(checkOut.getCheckOutTime());
        assertEquals("Session completed cleanly", checkOut.getRemarks());
    }

    @Test
    @DisplayName("TC-06: Society admin daily attendance roll call")
    void testDailySocietyAttendance() {
        LogAttendanceRequest checkInReq = new LogAttendanceRequest(701L, "ON_TIME", "Morning slot attendance");
        attendanceService.logAttendance(101L, checkInReq);

        LocalDate bookingDate = bookingTable.get(701L).getBookingDate();
        List<AttendanceDto> dailyList = attendanceService.getDailySocietyAttendance(1L, bookingDate);

        assertEquals(1, dailyList.size());
        assertEquals("Sunita Devi", dailyList.get(0).getCookName());
        assertEquals("Flat 302", dailyList.get(0).getFlatNumber());
    }
}

