package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.AdminRole;
import com.nexsol.tpa.core.enums.ServiceType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record AdminUser(Long id, String loginId, String password, String name, AdminRole role,
        Set<ServiceType> serviceType, LocalDateTime createdAt) {

    public static AdminUser createNew(String loginId, String encodedPassword, String name, AdminRole role,
            Set<ServiceType> serviceType) {
        return AdminUser.builder()
            .loginId(loginId)
            .password(encodedPassword)
            .name(name)
            .role(role)
            .serviceType(serviceType)
            .build();
    }

    public AdminUser withNewPassword(String newEncodedPassword) {
        return new AdminUser(this.id, this.loginId, newEncodedPassword, this.name, this.role, this.serviceType,
                this.createdAt);
    }
}
