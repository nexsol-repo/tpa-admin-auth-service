package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.support.security.PasswordHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BCryptPasswordEncoder implements PasswordEncoder {

    private final PasswordHasher passwordHasher;

    @Override
    public String encode(String password) {
        return passwordHasher.hash(password);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordHasher.matches(rawPassword, encodedPassword);
    }

}
