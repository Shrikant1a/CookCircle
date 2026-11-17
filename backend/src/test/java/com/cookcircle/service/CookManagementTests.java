package com.cookcircle.service;

import com.cookcircle.dto.cook.AssignCookRequest;
import com.cookcircle.dto.cook.CookAssignmentDto;
import com.cookcircle.dto.cook.CookDto;
import com.cookcircle.dto.cook.CreateCookRequest;
import com.cookcircle.dto.cook.UpdateCookProfileRequest;
import com.cookcircle.entity.*;
import com.cookcircle.exception.ResourceNotFoundException;
import com.cookcircle.repository.*;
import com.cookcircle.service.impl.CookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CookManagementTests {

    private final Map<Long, Cook> cookTable = new HashMap<>();
    private final Map<Long, CookAssignment> assignmentTable = new HashMap<>();
    private final Map<Long, Society> societyTable = new HashMap<>();
    private final Map<Long, Tower> towerTable = new HashMap<>();
    private final Map<Long, User> userTable = new HashMap<>();
    private final Map<Long, Role> roleTable = new HashMap<>();

    private CookRepository cookRepository;
    private CookAssignmentRepository cookAssignmentRepository;
    private SocietyRepository societyRepository;
    private TowerRepository towerRepository;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    private CookService cookService;

    @SuppressWarnings("unchecked")
    private <T> T createProxy(Class<T> intf, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(intf.getClassLoader(), new Class<?>[]{intf}, handler);
    }

    @BeforeEach
    void setUp() {
        cookTable.clear();
        assignmentTable.clear();
        societyTable.clear();
        towerTable.clear();
        userTable.clear();
        roleTable.clear();
        passwordEncoder = new BCryptPasswordEncoder();

        cookRepository = createProxy(CookRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("findById".equals(name)) {
                Long id = (Long) args[0];
                return Optional.ofNullable(cookTable.get(id));
            } else if ("findByUserId".equals(name)) {
                Long uId = (Long) args[0];
                return cookTable.values().stream()
                        .filter(c -> c.getUser() != null && c.getUser().getId().equals(uId))
                        .findFirst();
            } else if ("findBySocietyId".equals(name)) {
                Long socId = (Long) args[0];
                return cookTable.values().stream()
                        .filter(c -> c.getSociety() != null && c.getSociety().getId().equals(socId))
                        .toList();
            } else if ("findBySocietyIdAndType".equals(name)) {
                Long socId = (Long) args[0];
                String type = (String) args[1];
                return cookTable.values().stream()
                        .filter(c -> c.getSociety().getId().equals(socId) && c.getType().equalsIgnoreCase(type))
                        .toList();
            } else if ("findBySocietyIdAndStatus".equals(name)) {
                Long socId = (Long) args[0];
                String status = (String) args[1];
                return cookTable.values().stream()
                        .filter(c -> c.getSociety().getId().equals(socId) && c.getStatus().equalsIgnoreCase(status))
                        .toList();
            } else if ("save".equals(name)) {
                Cook c = (Cook) args[0];
                if (c.getId() == null) {
                    c.setId((long) (cookTable.size() + 1));
                }
                cookTable.put(c.getId(), c);
                return c;
            } else if ("existsById".equals(name)) {
                Long id = (Long) args[0];
                return cookTable.containsKey(id);
            }
            return null;
        });

        cookAssignmentRepository = createProxy(CookAssignmentRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("findByCookId".equals(name)) {
                Long cookId = (Long) args[0];
                return assignmentTable.values().stream()
                        .filter(a -> a.getCook().getId().equals(cookId))
                        .toList();
            } else if ("findByCookIdAndStatus".equals(name)) {
                Long cookId = (Long) args[0];
                String status = (String) args[1];
                return assignmentTable.values().stream()
                        .filter(a -> a.getCook().getId().equals(cookId) && a.getStatus().equalsIgnoreCase(status))
                        .toList();
            } else if ("findByTowerId".equals(name)) {
                Long towerId = (Long) args[0];
                return assignmentTable.values().stream()
                        .filter(a -> a.getTower().getId().equals(towerId))
                        .toList();
            } else if ("existsByCookIdAndTowerIdAndShiftAndStatus".equals(name)) {
                Long cookId = (Long) args[0];
                Long towerId = (Long) args[1];
                String shift = (String) args[2];
                String status = (String) args[3];
                return assignmentTable.values().stream()
                        .anyMatch(a -> a.getCook().getId().equals(cookId)
                                && a.getTower().getId().equals(towerId)
                                && a.getShift().equalsIgnoreCase(shift)
                                && a.getStatus().equalsIgnoreCase(status));
            } else if ("save".equals(name)) {
                CookAssignment a = (CookAssignment) args[0];
                if (a.getId() == null) {
                    a.setId((long) (assignmentTable.size() + 1));
                }
                assignmentTable.put(a.getId(), a);
                return a;
            } else if ("findById".equals(name)) {
                Long id = (Long) args[0];
                return Optional.ofNullable(assignmentTable.get(id));
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

        userRepository = createProxy(UserRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("existsByEmail".equals(name)) {
                String email = (String) args[0];
                return userTable.values().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
            } else if ("existsByPhone".equals(name)) {
                String phone = (String) args[0];
                return userTable.values().stream().anyMatch(u -> u.getPhone().equals(phone));
            } else if ("save".equals(name)) {
                User u = (User) args[0];
                if (u.getId() == null) {
                    u.setId((long) (userTable.size() + 1));
                }
                userTable.put(u.getId(), u);
                return u;
            }
            return null;
        });

        roleRepository = createProxy(RoleRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("findByName".equals(name)) {
                String rName = (String) args[0];
                return roleTable.values().stream().filter(r -> r.getName().equalsIgnoreCase(rName)).findFirst();
            } else if ("save".equals(name)) {
                Role r = (Role) args[0];
                if (r.getId() == null) {
                    r.setId((long) (roleTable.size() + 1));
                }
                roleTable.put(r.getId(), r);
                return r;
            }
            return null;
        });

        cookService = new CookServiceImpl(
                cookRepository,
                cookAssignmentRepository,
                societyRepository,
                towerRepository,
                userRepository,
                roleRepository,
                passwordEncoder
        );

        // Seed Society & Towers
        Society society = new Society();
        society.setId(1L);
        society.setCode("HG-POWAI");
        society.setName("Hiranandani Gardens");
        societyTable.put(1L, society);

        Tower towerA = new Tower();
        towerA.setId(10L);
        towerA.setName("Tower A");
        towerA.setSociety(society);
        towerTable.put(10L, towerA);

        Tower towerB = new Tower();
        towerB.setId(20L);
        towerB.setName("Tower B");
        towerB.setSociety(society);
        towerTable.put(20L, towerB);
    }

    @Test
    @DisplayName("TC-01: Successfully create and onboard a cook partner")
    void testCreateCookSuccess() {
        CreateCookRequest req = new CreateCookRequest();
        req.setFullName("Sunita Devi");
        req.setEmail("sunita.devi@cookcircle.com");
        req.setPhone("+91 98200 11223");
        req.setPassword("Sunita@123");
        req.setType("PRIMARY_COOK");
        req.setSpecialties("North Indian, Gujarati, Jain");
        req.setExperienceYears(8);
        req.setAadhaarVerified(true);
        req.setPoliceVerified(true);
        req.setVaccinationStatus("Double Vaccinated");

        CookDto created = cookService.createCook(1L, req);

        assertNotNull(created);
        assertEquals(1L, created.getId());
        assertEquals("Sunita Devi", created.getFullName());
        assertEquals("sunita.devi@cookcircle.com", created.getEmail());
        assertEquals("PRIMARY_COOK", created.getType());
        assertEquals(8, created.getExperienceYears());
        assertTrue(created.getPoliceVerified());
        assertTrue(created.getAadhaarVerified());
        assertEquals("ACTIVE", created.getStatus());
    }

    @Test
    @DisplayName("TC-02: Prevent duplicate cook onboarding by email or phone")
    void testDuplicateCookEmailPhone() {
        CreateCookRequest req1 = new CreateCookRequest();
        req1.setFullName("Sunita Devi");
        req1.setEmail("sunita.devi@cookcircle.com");
        req1.setPhone("+91 98200 11223");
        cookService.createCook(1L, req1);

        // Duplicate email
        CreateCookRequest req2 = new CreateCookRequest();
        req2.setFullName("Sunita Duplicate");
        req2.setEmail("sunita.devi@cookcircle.com");
        req2.setPhone("+91 98200 99999");
        assertThrows(IllegalArgumentException.class, () -> cookService.createCook(1L, req2));

        // Duplicate phone
        CreateCookRequest req3 = new CreateCookRequest();
        req3.setFullName("Another Cook");
        req3.setEmail("another.cook@cookcircle.com");
        req3.setPhone("+91 98200 11223");
        assertThrows(IllegalArgumentException.class, () -> cookService.createCook(1L, req3));
    }

    @Test
    @DisplayName("TC-03: Assign active cook to a tower block and retrieve assignments")
    void testAssignCookToTower() {
        CreateCookRequest req = new CreateCookRequest();
        req.setFullName("Sunita Devi");
        req.setEmail("sunita.devi@cookcircle.com");
        req.setPhone("+91 98200 11223");
        CookDto cook = cookService.createCook(1L, req);

        AssignCookRequest assignReq = new AssignCookRequest(10L, "MORNING", LocalDate.now(), null);
        CookAssignmentDto assignment = cookService.assignCookToTower(cook.getId(), assignReq);

        assertNotNull(assignment);
        assertEquals("Sunita Devi", assignment.getCookName());
        assertEquals("Tower A", assignment.getTowerName());
        assertEquals("MORNING", assignment.getShift());
        assertEquals("ACTIVE", assignment.getStatus());

        List<CookAssignmentDto> cookAssignments = cookService.getCookAssignments(cook.getId());
        assertEquals(1, cookAssignments.size());

        CookDto refreshedCook = cookService.getCookById(cook.getId());
        assertEquals(1, refreshedCook.getAssignedTowers().size());
        assertTrue(refreshedCook.getAssignedTowers().get(0).contains("Tower A"));
    }

    @Test
    @DisplayName("TC-04: Prevent duplicate active assignment for same cook, tower and shift")
    void testPreventDuplicateShiftAssignment() {
        CreateCookRequest req = new CreateCookRequest();
        req.setFullName("Sunita Devi");
        req.setEmail("sunita.devi@cookcircle.com");
        req.setPhone("+91 98200 11223");
        CookDto cook = cookService.createCook(1L, req);

        AssignCookRequest assignReq1 = new AssignCookRequest(10L, "MORNING", LocalDate.now(), null);
        cookService.assignCookToTower(cook.getId(), assignReq1);

        // Attempt exact duplicate assignment
        AssignCookRequest assignReq2 = new AssignCookRequest(10L, "MORNING", LocalDate.now(), null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            cookService.assignCookToTower(cook.getId(), assignReq2);
        });
        assertTrue(ex.getMessage().contains("already assigned"));
    }

    @Test
    @DisplayName("TC-05: Reject cross-society tower assignment")
    void testPreventCrossSocietyAssignment() {
        // Create Society 2 and Tower in Society 2
        Society society2 = new Society();
        society2.setId(2L);
        society2.setCode("PSN-WFD");
        societyTable.put(2L, society2);

        Tower towerS2 = new Tower();
        towerS2.setId(50L);
        towerS2.setName("Tower X");
        towerS2.setSociety(society2);
        towerTable.put(50L, towerS2);

        // Cook in Society 1
        CreateCookRequest req = new CreateCookRequest();
        req.setFullName("Sunita Devi");
        req.setEmail("sunita.devi@cookcircle.com");
        req.setPhone("+91 98200 11223");
        CookDto cook = cookService.createCook(1L, req);

        // Attempt assigning Cook from Society 1 to Tower in Society 2
        AssignCookRequest assignReq = new AssignCookRequest(50L, "MORNING", LocalDate.now(), null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            cookService.assignCookToTower(cook.getId(), assignReq);
        });
        assertTrue(ex.getMessage().contains("Cross-society"));
    }

    @Test
    @DisplayName("TC-06: Trust verification badges and status updates")
    void testTrustVerificationAndStatus() {
        CreateCookRequest req = new CreateCookRequest();
        req.setFullName("Ramesh Kumar");
        req.setEmail("ramesh.kumar@cookcircle.com");
        req.setPhone("+91 98200 44556");
        req.setType("BACKUP_FLOAT_COOK");
        req.setPoliceVerified(false);
        CookDto cook = cookService.createCook(1L, req);

        assertFalse(cook.getPoliceVerified());
        assertEquals("BACKUP_FLOAT_COOK", cook.getType());

        // Admin verifies badges
        CookDto verified = cookService.updateCookVerification(cook.getId(), true, true, "Booster Dose");
        assertTrue(verified.getPoliceVerified());
        assertEquals("Booster Dose", verified.getVaccinationStatus());

        // Status change to INACTIVE prevents tower assignment
        cookService.updateCookStatus(cook.getId(), "INACTIVE");
        CookDto inactive = cookService.getCookById(cook.getId());
        assertEquals("INACTIVE", inactive.getStatus());

        AssignCookRequest assignReq = new AssignCookRequest(10L, "MORNING", LocalDate.now(), null);
        assertThrows(IllegalArgumentException.class, () -> {
            cookService.assignCookToTower(cook.getId(), assignReq);
        });
    }
}

