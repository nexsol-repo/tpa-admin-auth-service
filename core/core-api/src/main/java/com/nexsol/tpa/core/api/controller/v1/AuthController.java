package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.request.LoginRequest;
import com.nexsol.tpa.core.api.controller.v1.request.RegisterRequest;
import com.nexsol.tpa.core.domain.AdminAuthService;
import com.nexsol.tpa.core.support.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AdminAuthService adminAuthService;

    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody LoginRequest request) {
        String token = adminAuthService.login(request.loginId(), request.password());

        return ApiResponse.success(token);
    }

    @PostMapping("/register")
    public ApiResponse<Long> register(@RequestBody RegisterRequest request) {
        Long userId = adminAuthService.register(request.loginId(), request.password(), request.name(), request.role(),
                request.serviceType());
        return ApiResponse.success(userId);
    }

}
