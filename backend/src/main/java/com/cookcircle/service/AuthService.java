package com.cookcircle.service;

import com.cookcircle.dto.auth.AuthResponse;
import com.cookcircle.dto.auth.LoginRequest;
import com.cookcircle.dto.auth.RegisterCookRequest;
import com.cookcircle.dto.auth.RegisterResidentRequest;
import com.cookcircle.dto.auth.UserSummaryDto;
import com.cookcircle.security.UserPrincipal;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse registerResident(RegisterResidentRequest request);
    AuthResponse registerCook(RegisterCookRequest request);
    UserSummaryDto getCurrentUser(UserPrincipal userPrincipal);
}

