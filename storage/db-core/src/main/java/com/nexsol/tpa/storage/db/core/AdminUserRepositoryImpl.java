package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.AdminUser;
import com.nexsol.tpa.core.domain.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AdminUserRepositoryImpl implements AdminUserRepository {

    private final AdminUserJpaRepository adminUserJpaRepository;

    @Override
    public AdminUser save(AdminUser adminUser) {
        AdminUserEntity entity = AdminUserEntity.fromDomain(adminUser);

        AdminUserEntity saved = adminUserJpaRepository.save(entity);

        return saved.toDomain();
    }

    @Override
    public Optional<AdminUser> findByLoginId(String loginId) {
        return adminUserJpaRepository.findByLoginId(loginId).map(AdminUserEntity::toDomain);
    }

    @Override
    public boolean existsByLoginId(String loginId) {
        return adminUserJpaRepository.existsByLoginId(loginId);
    }

}
