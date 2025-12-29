package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.AdminUser;
import com.nexsol.tpa.core.enums.AdminRole;
import com.nexsol.tpa.core.enums.ServiceType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "admin_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminUserEntity extends BaseEntity {

    private String loginId;

    private String password;

    private String name;

    @Enumerated(EnumType.STRING)
    private AdminRole role;

    @Column(name = "service_type")
    private String serviceType;


    public static AdminUserEntity fromDomain(AdminUser domain) {
        AdminUserEntity entity = new AdminUserEntity();
        entity.loginId = domain.loginId();
        entity.password = domain.password();
        entity.name = domain.name();
        entity.role = domain.role();
        entity.serviceType = domain.serviceType() != null
                ? domain.serviceType().stream().map(Enum::name).collect(Collectors.joining(","))
                : "";
        return entity;
    }

    public AdminUser toDomain() {
        Set<ServiceType> services = StringUtils.hasText(this.serviceType)
                ? Arrays.stream(this.serviceType.split(","))
                .map(ServiceType::valueOf)
                .collect(Collectors.toSet()) : Collections.emptySet();
        return AdminUser.builder()
                .id(this.getId())
                .loginId(this.loginId)
                .password(this.password)
                .name(this.name)
                .role(this.role)
                .serviceType(services)
                .build();
    }

}
