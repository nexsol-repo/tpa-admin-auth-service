package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.ServiceType;
import com.nexsol.tpa.support.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenIssuer implements TokenIssuer {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public String issueToken(Long id, String role, Set<ServiceType> serviceType) {
        Set<String> scopes = serviceType.stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
        return jwtTokenProvider.generate(id, role,scopes,Map.of());
    }

}
