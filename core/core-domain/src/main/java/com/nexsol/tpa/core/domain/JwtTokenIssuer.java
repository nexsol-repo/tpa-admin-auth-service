package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.support.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtTokenIssuer implements TokenIssuer {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public String issueToken(Long id, String role) {
        return jwtTokenProvider.generate(id, role, Map.of());
    }

}
