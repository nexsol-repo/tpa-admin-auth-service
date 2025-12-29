package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminUserReader {

    private final AdminUserRepository adminUserRepository;

    public AdminUser read(String loginId) {
        return adminUserRepository.findByLoginId(loginId)
            .orElseThrow(() -> new CoreException(CoreErrorType.USER_NOT_FOUND));
    }

    public boolean exists(String loginId) {
        return adminUserRepository.existsByLoginId(loginId);
    }

}
