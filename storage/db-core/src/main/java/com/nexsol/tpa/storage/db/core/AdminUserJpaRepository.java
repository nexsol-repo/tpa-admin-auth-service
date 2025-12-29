package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminUserJpaRepository extends JpaRepository<AdminUserEntity, Long> {

    Optional<AdminUserEntity> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

}
