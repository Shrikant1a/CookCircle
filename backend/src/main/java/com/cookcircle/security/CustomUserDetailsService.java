package com.cookcircle.security;

import com.cookcircle.entity.User;
import com.cookcircle.repository.UserRepository;
import com.cookcircle.repository.ResidentRepository;
import com.cookcircle.repository.CookRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ResidentRepository residentRepository;
    private final CookRepository cookRepository;

    public CustomUserDetailsService(UserRepository userRepository,
                                    ResidentRepository residentRepository,
                                    CookRepository cookRepository) {
        this.userRepository = userRepository;
        this.residentRepository = residentRepository;
        this.cookRepository = cookRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        Long societyId = resolveSocietyId(user);
        return UserPrincipal.create(user, societyId);
    }

    private Long resolveSocietyId(User user) {
        String role = user.getRole().getName();
        if (role.contains("RESIDENT")) {
            return residentRepository.findByUserId(user.getId())
                    .map(r -> r.getSociety().getId())
                    .orElse(null);
        } else if (role.contains("COOK")) {
            return cookRepository.findByUserId(user.getId())
                    .map(c -> c.getSociety().getId())
                    .orElse(null);
        }
        return null;
    }
}

