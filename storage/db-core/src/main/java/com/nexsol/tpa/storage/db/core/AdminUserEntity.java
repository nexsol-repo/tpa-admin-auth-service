package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.AdminUser;
import com.nexsol.tpa.core.enums.AdminRole;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admin_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminUserEntity extends BaseEntity {

    private String loginId;

    private String password;

    private String name;

    private AdminRole role;

    public static AdminUserEntity fromDomain(AdminUser domain) {
        AdminUserEntity entity = new AdminUserEntity();
        entity.loginId = domain.loginId();
        entity.password = domain.password();
        entity.name = domain.name();
        entity.role = domain.role();
        return entity;
    }

    public AdminUser toDomain() {
        return AdminUser.builder()
            .id(this.getId())
            .loginId(this.loginId)
            .password(this.password)
            .name(this.name)
            .role(this.role)
            .build();
    }

}
