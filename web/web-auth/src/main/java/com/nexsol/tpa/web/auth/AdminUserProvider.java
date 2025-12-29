package com.nexsol.tpa.web.auth;

import com.nexsol.tpa.core.enums.ServiceType;

import java.util.Set;

public record AdminUserProvider(Long id, String role, Set<ServiceType> serviceTypes) {
}
