package com.cookcircle.service;

import com.cookcircle.dto.complaint.ComplaintDto;
import com.cookcircle.dto.complaint.CreateComplaintRequest;
import com.cookcircle.dto.complaint.ResolveComplaintRequest;
import com.cookcircle.dto.notification.NotificationDto;
import com.cookcircle.dto.report.SocietyDashboardSummaryDto;
import com.cookcircle.entity.*;
import com.cookcircle.repository.*;
import com.cookcircle.service.impl.ComplaintServiceImpl;
import com.cookcircle.service.impl.NotificationServiceImpl;
import com.cookcircle.service.impl.ReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationAndReportTests {

    private final Map<Long, Notification> notificationTable = new HashMap<>();
    private final Map<Long, Complaint> complaintTable = new HashMap<>();
    private final Map<Long, Society> societyTable = new HashMap<>();
    private final Map<Long, Tower> towerTable = new HashMap<>();
    private final Map<Long, Apartment> apartmentTable = new HashMap<>();
    private final Map<Long, Resident> residentTable = new HashMap<>();
    private final Map<Long, Cook> cookTable = new HashMap<>();
    private final Map<Long, User> userTable = new HashMap<>();
    private final Map<Long, Invoice> invoiceTable = new HashMap<>();
    private final Map<Long, Attendance> attendanceTable = new HashMap<>();
    private final Map<Long, Booking> bookingTable = new HashMap<>();
    private final Map<Long, LeaveRequest> leaveTable = new HashMap<>();

    private NotificationRepository notificationRepository;
    private ComplaintRepository complaintRepository;
    private SocietyRepository societyRepository;
    private ApartmentRepository apartmentRepository;
    private ResidentRepository residentRepository;
    private CookRepository cookRepository;
    private BookingRepository bookingRepository;
    private LeaveRequestRepository leaveRequestRepository;
    private InvoiceRepository invoiceRepository;
    private AttendanceRepository attendanceRepository;
    private UserRepository userRepository;

    private NotificationService notificationService;
    private ComplaintService complaintService;
    private ReportService reportService;

    @SuppressWarnings("unchecked")
    private <T> T createProxy(Class<T> intf, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(intf.getClassLoader(), new Class<?>[]{intf}, handler);
    }

    @BeforeEach
    void setUp() {
        notificationTable.clear();
        complaintTable.clear();
        societyTable.clear();
        towerTable.clear();
        apartmentTable.clear();
        residentTable.clear();
        cookTable.clear();
        userTable.clear();
        invoiceTable.clear();
        attendanceTable.clear();
        bookingTable.clear();
        leaveTable.clear();

        notificationRepository = createProxy(NotificationRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("save".equals(name)) {
                Notification n = (Notification) args[0];
                if (n.getId() == null) {
                    n.setId((long) (notificationTable.size() + 1));
                }
                notificationTable.put(n.getId(), n);
                return n;
            } else if ("findById".equals(name)) {
                Long id = (Long) args[0];
                return Optional.ofNullable(notificationTable.get(id));
            } else if ("findByUserIdOrderByCreatedAtDesc".equals(name)) {
                Long uId = (Long) args[0];
                return notificationTable.values().stream().filter(n -> n.getUser().getId().equals(uId)).toList();
            } else if ("findByUserIdAndIsReadOrderByCreatedAtDesc".equals(name)) {
                Long uId = (Long) args[0];
                Boolean isRead = (Boolean) args[1];
                return notificationTable.values().stream()
                        .filter(n -> n.getUser().getId().equals(uId) && n.getIsRead().equals(isRead))
                        .toList();
            } else if ("countByUserIdAndIsRead".equals(name)) {
                Long uId = (Long) args[0];
                Boolean isRead = (Boolean) args[1];
                return notificationTable.values().stream()
                        .filter(n -> n.getUser().getId().equals(uId) && n.getIsRead().equals(isRead))
                        .count();
            }
            return null;
        });

        complaintRepository = createProxy(ComplaintRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("save".equals(name)) {
                Complaint c = (Complaint) args[0];
                if (c.getId() == null) {
                    c.setId((long) (complaintTable.size() + 1));
                }
                complaintTable.put(c.getId(), c);
                return c;
            } else if ("findById".equals(name)) {
                Long id = (Long) args[0];
                return Optional.ofNullable(complaintTable.get(id));
            } else if ("findByResidentIdOrderByCreatedAtDesc".equals(name)) {
                Long rId = (Long) args[0];
                return complaintTable.values().stream().filter(c -> c.getResident().getId().equals(rId)).toList();
            } else if ("findByResidentSocietyIdOrderByCreatedAtDesc".equals(name)) {
                Long sId = (Long) args[0];
                return complaintTable.values().stream()
                        .filter(c -> c.getResident().getSociety().getId().equals(sId))
                        .toList();
            }
            return null;
        });

        societyRepository = createProxy(SocietyRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("findById".equals(name)) {
                Long id = (Long) args[0];
                return Optional.ofNullable(societyTable.get(id));
            } else if ("existsById".equals(name)) {
                Long id = (Long) args[0];
                return societyTable.containsKey(id);
            }
            return null;
        });

        apartmentRepository = createProxy(ApartmentRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("findByTowerSocietyId".equals(name)) {
                Long sId = (Long) args[0];
                return apartmentTable.values().stream()
                        .filter(a -> a.getTower().getSociety().getId().equals(sId))
                        .toList();
            }
            return null;
        });

        residentRepository = createProxy(ResidentRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("findByUserId".equals(name)) {
                Long uId = (Long) args[0];
                return residentTable.values().stream().filter(r -> r.getUser().getId().equals(uId)).findFirst();
            } else if ("findBySocietyId".equals(name)) {
                Long sId = (Long) args[0];
                return residentTable.values().stream().filter(r -> r.getSociety().getId().equals(sId)).toList();
            }
            return null;
        });

        cookRepository = createProxy(CookRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("findBySocietyId".equals(name)) {
                Long sId = (Long) args[0];
                return cookTable.values().stream().filter(c -> c.getSociety().getId().equals(sId)).toList();
            } else if ("findBySocietyIdAndType".equals(name)) {
                Long sId = (Long) args[0];
                String type = (String) args[1];
                return cookTable.values().stream()
                        .filter(c -> c.getSociety().getId().equals(sId) && c.getType().equalsIgnoreCase(type))
                        .toList();
            }
            return null;
        });

        bookingRepository = createProxy(BookingRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("findByEffectiveCookIdAndBookingDate".equals(name)) {
                Long cId = (Long) args[0];
                LocalDate d = (LocalDate) args[1];
                return bookingTable.values().stream()
                        .filter(b -> b.getEffectiveCook().getId().equals(cId) && b.getBookingDate().equals(d))
                        .toList();
            }
            return null;
        });

        leaveRequestRepository = createProxy(LeaveRequestRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("findByCookSocietyIdAndStatus".equals(name)) {
                Long sId = (Long) args[0];
                String st = (String) args[1];
                return leaveTable.values().stream()
                        .filter(l -> l.getCook().getSociety().getId().equals(sId) && l.getStatus().equalsIgnoreCase(st))
                        .toList();
            }
            return null;
        });

        invoiceRepository = createProxy(InvoiceRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("findBySocietyId".equals(name)) {
                Long sId = (Long) args[0];
                return invoiceTable.values().stream().filter(i -> i.getSociety().getId().equals(sId)).toList();
            } else if ("findBySocietyIdAndBillingMonth".equals(name)) {
                Long sId = (Long) args[0];
                String m = (String) args[1];
                return invoiceTable.values().stream()
                        .filter(i -> i.getSociety().getId().equals(sId) && i.getBillingMonth().equals(m))
                        .toList();
            }
            return null;
        });

        attendanceRepository = createProxy(AttendanceRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("findByCookIdAndAttendanceDate".equals(name)) {
                Long cId = (Long) args[0];
                LocalDate d = (LocalDate) args[1];
                return attendanceTable.values().stream()
                        .filter(a -> a.getCook().getId().equals(cId) && a.getAttendanceDate().equals(d))
                        .toList();
            }
            return null;
        });

        userRepository = createProxy(UserRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("findById".equals(name)) {
                Long id = (Long) args[0];
                return Optional.ofNullable(userTable.get(id));
            } else if ("existsById".equals(name)) {
                Long id = (Long) args[0];
                return userTable.containsKey(id);
            }
            return null;
        });

        notificationService = new NotificationServiceImpl(notificationRepository, userRepository);
        complaintService = new ComplaintServiceImpl(complaintRepository, residentRepository, societyRepository);
        reportService = new ReportServiceImpl(
                societyRepository,
                apartmentRepository,
                residentRepository,
                cookRepository,
                bookingRepository,
                leaveRequestRepository,
                invoiceRepository,
                attendanceRepository
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

        // Seed Resident
        User user = new User();
        user.setId(101L);
        user.setFullName("Aarav Sharma");
        user.setEmail("aarav.sharma@example.com");
        userTable.put(101L, user);

        Apartment apt = new Apartment();
        apt.setId(201L);
        apt.setTower(tower);
        apt.setFlatNumber("Flat 302");
        apartmentTable.put(201L, apt);

        Resident resident = new Resident();
        resident.setId(301L);
        resident.setUser(user);
        resident.setSociety(society);
        resident.setApartment(apt);
        residentTable.put(301L, resident);

        // Seed Cooks
        User cookUser = new User();
        cookUser.setId(401L);
        cookUser.setFullName("Sunita Devi");
        userTable.put(401L, cookUser);

        Cook cook = new Cook();
        cook.setId(501L);
        cook.setUser(cookUser);
        cook.setSociety(society);
        cook.setType("PRIMARY_COOK");
        cookTable.put(501L, cook);

        User backupUser = new User();
        backupUser.setId(402L);
        backupUser.setFullName("Ramesh Kumar");
        userTable.put(402L, backupUser);

        Cook backupCook = new Cook();
        backupCook.setId(502L);
        backupCook.setUser(backupUser);
        backupCook.setSociety(society);
        backupCook.setType("BACKUP_FLOAT_COOK");
        cookTable.put(502L, backupCook);

        // Seed Invoice
        Invoice invoice = new Invoice();
        invoice.setId(801L);
        invoice.setInvoiceNumber("INV-202611-Flat302");
        invoice.setSociety(society);
        invoice.setApartment(apt);
        invoice.setResident(resident);
        invoice.setBillingMonth("2026-11");
        invoice.setBaseAmount(new BigDecimal("4200.00"));
        invoice.setDiscountWallet(new BigDecimal("280.00"));
        invoice.setNetAmount(new BigDecimal("3920.00"));
        invoice.setStatus("PAID");
        invoiceTable.put(801L, invoice);
    }

    @Test
    @DisplayName("TC-01: Send notification and track unread count")
    void testSendAndRetrieveNotifications() {
        NotificationDto n1 = notificationService.sendNotification(
                101L,
                "Cooking Slot Confirmed",
                "Your morning cooking slot for Flat 302 is confirmed. OTP: 4829",
                "BOOKING"
        );

        assertNotNull(n1);
        assertEquals(1L, n1.getId());
        assertEquals("Cooking Slot Confirmed", n1.getTitle());
        assertFalse(n1.getIsRead());

        assertEquals(1, notificationService.getUnreadCount(101L));

        notificationService.markAsRead(n1.getId(), 101L);
        assertEquals(0, notificationService.getUnreadCount(101L));
    }

    @Test
    @DisplayName("TC-02: Mark all notifications as read")
    void testMarkAllNotificationsAsRead() {
        notificationService.sendNotification(101L, "Alert 1", "Message 1", "GENERAL");
        notificationService.sendNotification(101L, "Alert 2", "Message 2", "BACKUP");

        assertEquals(2, notificationService.getUnreadCount(101L));

        notificationService.markAllAsRead(101L);
        assertEquals(0, notificationService.getUnreadCount(101L));
    }

    @Test
    @DisplayName("TC-03: Resident creates complaint and admin resolves it")
    void testCreateAndResolveComplaint() {
        CreateComplaintRequest req = new CreateComplaintRequest(
                "Taste",
                "Food too spicy",
                "Please instruct cook to reduce green chillies in dal."
        );

        ComplaintDto created = complaintService.createComplaint(101L, req);
        assertNotNull(created);
        assertEquals("OPEN", created.getStatus());
        assertEquals("Taste", created.getCategory());
        assertEquals("Flat 302", created.getFlatNumber());

        // Admin resolves
        ResolveComplaintRequest resolveReq = new ResolveComplaintRequest("Discussed with cook partner Sunita Devi. Dietary spice preference noted.");
        ComplaintDto resolved = complaintService.resolveComplaint(1L, created.getId(), resolveReq);

        assertEquals("RESOLVED", resolved.getStatus());
        assertEquals("Discussed with cook partner Sunita Devi. Dietary spice preference noted.", resolved.getResolutionNotes());
    }

    @Test
    @DisplayName("TC-04: Society dashboard KPI summary calculations")
    void testSocietyDashboardSummary() {
        SocietyDashboardSummaryDto summary = reportService.getSocietyDashboardSummary(1L);

        assertNotNull(summary);
        assertEquals("Hiranandani Gardens", summary.getSocietyName());
        assertEquals(1, summary.getTotalApartments());
        assertEquals(1, summary.getTotalResidents());
        assertEquals(2, summary.getTotalCooks());
        assertEquals(1, summary.getBackupCooks());
        assertEquals(new BigDecimal("3920.00"), summary.getMonthlyCollection());
        assertEquals(BigDecimal.ZERO, summary.getPendingCollection());
        assertEquals(94.5, summary.getPoolUtilizationRate());
        assertEquals(98.2, summary.getOnTimeSlaRate());
    }

    @Test
    @DisplayName("TC-05: Generate monthly invoices CSV export report")
    void testGenerateInvoicesCsv() {
        String csv = reportService.generateInvoicesCsv(1L, "2026-11");

        assertNotNull(csv);
        assertTrue(csv.contains("InvoiceNumber,FlatNumber,ResidentName,BillingMonth"));
        assertTrue(csv.contains("INV-202611-Flat302"));
        assertTrue(csv.contains("Flat 302"));
        assertTrue(csv.contains("3920.00"));
        assertTrue(csv.contains("PAID"));
    }

    @Test
    @DisplayName("TC-06: Generate cook daily attendance CSV export report")
    void testGenerateAttendanceCsv() {
        LocalDate today = LocalDate.now();

        CookingSlot slot = new CookingSlot();
        slot.setTower(towerTable.get(10L));

        Booking booking = new Booking();
        booking.setId(701L);
        booking.setSlot(slot);
        booking.setApartment(apartmentTable.get(201L));

        Attendance attendance = new Attendance();
        attendance.setId(1L);
        attendance.setCook(cookTable.get(501L));
        attendance.setBooking(booking);
        attendance.setAttendanceDate(today);
        attendance.setStatus("ON_TIME");
        attendance.setRemarks("Punctual arrival");
        attendanceTable.put(1L, attendance);

        String csv = reportService.generateAttendanceCsv(1L, today);

        assertNotNull(csv);
        assertTrue(csv.contains("CookName,BookingId,FlatNumber,TowerName"));
        assertTrue(csv.contains("Sunita Devi"));
        assertTrue(csv.contains("Flat 302"));
        assertTrue(csv.contains("ON_TIME"));
        assertTrue(csv.contains("Punctual arrival"));
    }
}

