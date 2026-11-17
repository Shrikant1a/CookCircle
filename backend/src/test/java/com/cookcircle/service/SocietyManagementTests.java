package com.cookcircle.service;

import com.cookcircle.dto.apartment.ApartmentDto;
import com.cookcircle.dto.apartment.CreateApartmentRequest;
import com.cookcircle.dto.resident.ResidentDto;
import com.cookcircle.dto.resident.UpdateResidentPreferencesRequest;
import com.cookcircle.dto.society.CreateSocietyRequest;
import com.cookcircle.dto.society.CreateTowerRequest;
import com.cookcircle.dto.society.SocietyDto;
import com.cookcircle.dto.society.TowerDto;
import com.cookcircle.entity.*;
import com.cookcircle.exception.ResourceNotFoundException;
import com.cookcircle.repository.*;
import com.cookcircle.service.impl.ResidentServiceImpl;
import com.cookcircle.service.impl.SocietyServiceImpl;
import com.cookcircle.service.impl.TowerApartmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SocietyManagementTests {

    private final Map<Long, Society> societyTable = new HashMap<>();
    private final Map<Long, Tower> towerTable = new HashMap<>();
    private final Map<Long, Apartment> apartmentTable = new HashMap<>();
    private final Map<Long, Resident> residentTable = new HashMap<>();
    private final Map<Long, User> userTable = new HashMap<>();

    private SocietyRepository societyRepository;
    private TowerRepository towerRepository;
    private ApartmentRepository apartmentRepository;
    private ResidentRepository residentRepository;
    private UserRepository userRepository;

    private SocietyService societyService;
    private TowerApartmentService towerApartmentService;
    private ResidentService residentService;

    @SuppressWarnings("unchecked")
    private <T> T createProxy(Class<T> intf, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(intf.getClassLoader(), new Class<?>[]{intf}, handler);
    }

    @BeforeEach
    void setUp() {
        societyTable.clear();
        towerTable.clear();
        apartmentTable.clear();
        residentTable.clear();
        userTable.clear();

        // SocietyRepository proxy
        societyRepository = createProxy(SocietyRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("existsByCode".equals(name)) {
                String code = (String) args[0];
                return societyTable.values().stream().anyMatch(s -> s.getCode().equalsIgnoreCase(code));
            } else if ("save".equals(name)) {
                Society s = (Society) args[0];
                if (s.getId() == null) {
                    s.setId((long) (societyTable.size() + 1));
                }
                societyTable.put(s.getId(), s);
                return s;
            } else if ("findById".equals(name)) {
                Long id = (Long) args[0];
                return Optional.ofNullable(societyTable.get(id));
            } else if ("findAll".equals(name)) {
                return new ArrayList<>(societyTable.values());
            } else if ("existsById".equals(name)) {
                Long id = (Long) args[0];
                return societyTable.containsKey(id);
            }
            return null;
        });

        // TowerRepository proxy
        towerRepository = createProxy(TowerRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("findBySocietyId".equals(name)) {
                Long socId = (Long) args[0];
                return towerTable.values().stream()
                        .filter(t -> t.getSociety() != null && t.getSociety().getId().equals(socId))
                        .toList();
            } else if ("existsBySocietyIdAndName".equals(name)) {
                Long socId = (Long) args[0];
                String tName = (String) args[1];
                return towerTable.values().stream()
                        .anyMatch(t -> t.getSociety().getId().equals(socId) && t.getName().equalsIgnoreCase(tName));
            } else if ("save".equals(name)) {
                Tower t = (Tower) args[0];
                if (t.getId() == null) {
                    t.setId((long) (towerTable.size() + 1));
                }
                towerTable.put(t.getId(), t);
                return t;
            } else if ("findById".equals(name)) {
                Long id = (Long) args[0];
                return Optional.ofNullable(towerTable.get(id));
            } else if ("existsById".equals(name)) {
                Long id = (Long) args[0];
                return towerTable.containsKey(id);
            }
            return null;
        });

        // ApartmentRepository proxy
        apartmentRepository = createProxy(ApartmentRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("findByTowerId".equals(name)) {
                Long tId = (Long) args[0];
                return apartmentTable.values().stream()
                        .filter(a -> a.getTower() != null && a.getTower().getId().equals(tId))
                        .toList();
            } else if ("existsByTowerIdAndFlatNumber".equals(name)) {
                Long tId = (Long) args[0];
                String flatNo = (String) args[1];
                return apartmentTable.values().stream()
                        .anyMatch(a -> a.getTower().getId().equals(tId) && a.getFlatNumber().equalsIgnoreCase(flatNo));
            } else if ("save".equals(name)) {
                Apartment a = (Apartment) args[0];
                if (a.getId() == null) {
                    a.setId((long) (apartmentTable.size() + 1));
                }
                apartmentTable.put(a.getId(), a);
                return a;
            } else if ("findById".equals(name)) {
                Long id = (Long) args[0];
                return Optional.ofNullable(apartmentTable.get(id));
            } else if ("findByTowerSocietyId".equals(name)) {
                Long socId = (Long) args[0];
                return apartmentTable.values().stream()
                        .filter(a -> a.getTower() != null && a.getTower().getSociety() != null && a.getTower().getSociety().getId().equals(socId))
                        .toList();
            }
            return null;
        });

        // ResidentRepository proxy
        residentRepository = createProxy(ResidentRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("findByUserId".equals(name)) {
                Long uId = (Long) args[0];
                return residentTable.values().stream()
                        .filter(r -> r.getUser() != null && r.getUser().getId().equals(uId))
                        .findFirst();
            } else if ("findById".equals(name)) {
                Long id = (Long) args[0];
                return Optional.ofNullable(residentTable.get(id));
            } else if ("findByApartmentId".equals(name)) {
                Long aptId = (Long) args[0];
                return residentTable.values().stream()
                        .filter(r -> r.getApartment() != null && r.getApartment().getId().equals(aptId))
                        .findFirst();
            } else if ("findBySocietyId".equals(name)) {
                Long socId = (Long) args[0];
                return residentTable.values().stream()
                        .filter(r -> r.getSociety() != null && r.getSociety().getId().equals(socId))
                        .toList();
            } else if ("save".equals(name)) {
                Resident r = (Resident) args[0];
                if (r.getId() == null) {
                    r.setId((long) (residentTable.size() + 1));
                }
                residentTable.put(r.getId(), r);
                return r;
            }
            return null;
        });

        // UserRepository proxy
        userRepository = createProxy(UserRepository.class, (proxy, method, args) -> {
            String name = method.getName();
            if ("save".equals(name)) {
                User u = (User) args[0];
                if (u.getId() == null) {
                    u.setId((long) (userTable.size() + 1));
                }
                userTable.put(u.getId(), u);
                return u;
            }
            return null;
        });

        societyService = new SocietyServiceImpl(societyRepository, towerRepository);
        towerApartmentService = new TowerApartmentServiceImpl(societyRepository, towerRepository, apartmentRepository, residentRepository);
        residentService = new ResidentServiceImpl(residentRepository, societyRepository, userRepository);
    }

    @Test
    @DisplayName("TC-01: Successfully create a new society with unique code")
    void testCreateSocietySuccess() {
        CreateSocietyRequest request = new CreateSocietyRequest();
        request.setCode("HG-POWAI");
        request.setName("Hiranandani Gardens");
        request.setAddressLine("Central Avenue, Powai");
        request.setCity("Mumbai");
        request.setState("Maharashtra");
        request.setPostalCode("400076");
        request.setBasePoolingFee(new BigDecimal("4200.00"));
        request.setSkipMealCredit(new BigDecimal("140.00"));

        SocietyDto result = societyService.createSociety(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("HG-POWAI", result.getCode());
        assertEquals("Hiranandani Gardens", result.getName());
        assertEquals("ACTIVE", result.getStatus());
        assertEquals(new BigDecimal("4200.00"), result.getBasePoolingFee());
        assertEquals(new BigDecimal("140.00"), result.getSkipMealCredit());
    }

    @Test
    @DisplayName("TC-02: Prevent duplicate society code creation")
    void testCreateSocietyDuplicateCodeThrows() {
        CreateSocietyRequest req1 = new CreateSocietyRequest();
        req1.setCode("HG-POWAI");
        req1.setName("Hiranandani Gardens");
        req1.setAddressLine("Central Avenue");
        req1.setCity("Mumbai");
        req1.setState("Maharashtra");
        req1.setPostalCode("400076");
        societyService.createSociety(req1);

        CreateSocietyRequest req2 = new CreateSocietyRequest();
        req2.setCode("HG-POWAI");
        req2.setName("Duplicate Society");
        req2.setAddressLine("Another road");
        req2.setCity("Mumbai");
        req2.setState("Maharashtra");
        req2.setPostalCode("400076");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            societyService.createSociety(req2);
        });

        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    @DisplayName("TC-03: Retrieve society by ID or throw ResourceNotFoundException")
    void testGetSocietyById() {
        CreateSocietyRequest req = new CreateSocietyRequest();
        req.setCode("PSN-WFD");
        req.setName("Prestige Shantiniketan");
        req.setAddressLine("Whitefield Main Rd");
        req.setCity("Bengaluru");
        req.setState("Karnataka");
        req.setPostalCode("560048");
        SocietyDto created = societyService.createSociety(req);

        SocietyDto found = societyService.getSocietyById(created.getId());
        assertEquals("PSN-WFD", found.getCode());
        assertEquals("Prestige Shantiniketan", found.getName());

        assertThrows(ResourceNotFoundException.class, () -> {
            societyService.getSocietyById(999L);
        });
    }

    @Test
    @DisplayName("TC-04: Add tower to society and prevent duplicate tower names within same society")
    void testCreateTowerAndDuplicateCheck() {
        CreateSocietyRequest socReq = new CreateSocietyRequest();
        socReq.setCode("HG-POWAI");
        socReq.setName("Hiranandani Gardens");
        socReq.setAddressLine("Central Ave");
        socReq.setCity("Mumbai");
        socReq.setState("Maharashtra");
        socReq.setPostalCode("400076");
        SocietyDto society = societyService.createSociety(socReq);

        CreateTowerRequest towerReq1 = new CreateTowerRequest("Tower A", 24);
        TowerDto t1 = towerApartmentService.createTower(society.getId(), towerReq1);

        assertNotNull(t1);
        assertEquals("Tower A", t1.getName());
        assertEquals(24, t1.getTotalFloors());

        // Duplicate tower in same society must fail
        CreateTowerRequest dupTowerReq = new CreateTowerRequest("Tower A", 20);
        assertThrows(IllegalArgumentException.class, () -> {
            towerApartmentService.createTower(society.getId(), dupTowerReq);
        });
    }

    @Test
    @DisplayName("TC-05: Add apartment to tower and prevent duplicate flat number")
    void testCreateApartmentAndDuplicateCheck() {
        CreateSocietyRequest socReq = new CreateSocietyRequest();
        socReq.setCode("HG-POWAI");
        socReq.setName("Hiranandani Gardens");
        socReq.setAddressLine("Central Ave");
        socReq.setCity("Mumbai");
        socReq.setState("Maharashtra");
        socReq.setPostalCode("400076");
        SocietyDto society = societyService.createSociety(socReq);

        CreateTowerRequest towerReq = new CreateTowerRequest("Tower A", 20);
        TowerDto tower = towerApartmentService.createTower(society.getId(), towerReq);

        CreateApartmentRequest aptReq1 = new CreateApartmentRequest("Flat 402", 4, "3BHK");
        ApartmentDto a1 = towerApartmentService.createApartment(tower.getId(), aptReq1);

        assertNotNull(a1);
        assertEquals("Flat 402", a1.getFlatNumber());
        assertEquals("3BHK", a1.getConfiguration());

        // Duplicate flat in same tower must fail
        CreateApartmentRequest dupAptReq = new CreateApartmentRequest("Flat 402", 4, "2BHK");
        assertThrows(IllegalArgumentException.class, () -> {
            towerApartmentService.createApartment(tower.getId(), dupAptReq);
        });
    }

    @Test
    @DisplayName("TC-06: Resident retrieval by user ID and preference updates")
    void testResidentServiceOperations() {
        Society society = new Society();
        society.setId(1L);
        society.setName("Hiranandani Gardens");
        societyTable.put(1L, society);

        Tower tower = new Tower();
        tower.setId(10L);
        tower.setName("Tower A");
        tower.setSociety(society);
        towerTable.put(10L, tower);

        Apartment apt = new Apartment();
        apt.setId(100L);
        apt.setFlatNumber("Flat 402");
        apt.setFloorNumber(4);
        apt.setConfiguration("3BHK");
        apt.setTower(tower);
        apartmentTable.put(100L, apt);

        Role role = new Role("ROLE_RESIDENT", "Resident");
        User user = new User();
        user.setId(50L);
        user.setEmail("resident.test@cookcircle.com");
        user.setFullName("Pooja Nair");
        user.setPhone("+91 98765 43210");
        user.setRole(role);
        userTable.put(50L, user);

        Resident resident = new Resident();
        resident.setId(200L);
        resident.setUser(user);
        resident.setSociety(society);
        resident.setApartment(apt);
        resident.setHouseholdSize("4 members");
        resident.setDietaryPref("Vegetarian (Jain)");
        resident.setWalletBalance(new BigDecimal("420.00"));
        residentTable.put(200L, resident);

        ResidentDto fetched = residentService.getResidentByUserId(50L);
        assertNotNull(fetched);
        assertEquals("Pooja Nair", fetched.getFullName());
        assertEquals("Flat 402", fetched.getFlatNumber());
        assertEquals("Vegetarian (Jain)", fetched.getDietaryPref());

        UpdateResidentPreferencesRequest updateReq = new UpdateResidentPreferencesRequest("5 members", "Vegan", "+91 91234 56789");
        ResidentDto updated = residentService.updatePreferences(200L, updateReq);

        assertEquals("5 members", updated.getHouseholdSize());
        assertEquals("Vegan", updated.getDietaryPref());
        assertEquals("+91 91234 56789", user.getPhone());
    }
}
