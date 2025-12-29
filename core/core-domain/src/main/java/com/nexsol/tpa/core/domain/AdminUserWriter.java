package com.nexsol.tpa.core.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminUserWriter {

    private final AdminUserRepository adminUserRepository;

    public AdminUser write(AdminUser adminUser) {
        return adminUserRepository.save(adminUser);
    }

}
