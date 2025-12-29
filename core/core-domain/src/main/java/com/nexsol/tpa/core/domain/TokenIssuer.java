package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.ServiceType;

import java.util.Set;

public interface TokenIssuer {

    String issueToken(Long id, String role, Set<ServiceType> serviceType);

}
