package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.AdminRole;
import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminUserReader adminUserReader;

    private final AdminUserWriter adminUserWriter;

    private final TokenIssuer tokenIssuer;

    private final PasswordEncoder passwordEncoder;

    public String login(String loginId, String password) {

        AdminUser user = adminUserReader.read(loginId);

        if (!passwordEncoder.matches(password, user.password())) {
            throw new CoreException(CoreErrorType.INVALID_PASSWORD);
        }

        return tokenIssuer.issueToken(user.id(), user.role().name());
    }

    public Long register(String loginId, String password, String name, AdminRole role) {
        if (adminUserReader.exists(loginId)) {
            throw new CoreException(CoreErrorType.USER_EXIST_DATA);
        }

        String encoded = passwordEncoder.encode(password);
        AdminUser user = AdminUser.createNew(loginId, encoded, name, role);

        return adminUserWriter.write(user).id();
    }

}
