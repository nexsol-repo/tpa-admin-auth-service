package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.enums.AdminRole;
import com.nexsol.tpa.core.enums.ServiceType;

import java.util.Set;

public record RegisterRequest(String loginId, String password, String name, AdminRole role,
        Set<ServiceType> serviceType) {
}
