package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.AdminRole;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AdminUser(Long id, String loginId, String password, String name, AdminRole role,
        LocalDateTime createdAt) {

    public static AdminUser createNew(String loginId, String encodedPassword, String name, AdminRole role) {
        return new AdminUser(null, loginId, encodedPassword, name, role, LocalDateTime.now());
    }

    public AdminUser withNewPassword(String newEncodedPassword) {
        return new AdminUser(this.id, this.loginId, newEncodedPassword, this.name, this.role, this.createdAt);
    }
}
