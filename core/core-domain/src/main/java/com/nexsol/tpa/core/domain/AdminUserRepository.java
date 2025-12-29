package com.nexsol.tpa.core.domain;

import java.util.Optional;

public interface AdminUserRepository {

    AdminUser save(AdminUser adminUser);

    Optional<AdminUser> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

}
