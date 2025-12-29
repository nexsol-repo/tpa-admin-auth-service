package com.nexsol.tpa.web.auth;


import com.nexsol.tpa.core.enums.ServiceType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginAdminArgumentResolver implements HandlerMethodArgumentResolver {


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginAdmin.class)
                && AdminUserProvider.class.isAssignableFrom(parameter.getParameterType());

    }

    @Override
    public @Nullable Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
                                            NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        String userIdHeader = request.getHeader("X-User-Id");
        String roleHeader = request.getHeader("X-User-Role");
        String scopeHeader = request.getHeader("X-User-Scope");

        if (!StringUtils.hasText(userIdHeader)) {
            log.warn("인증 헤더가 없습니다. Gateway를 확인해주세요URI: {}", request.getRequestURI());
            return null;
        }

        Long userId = Long.parseLong(userIdHeader);
        String role = roleHeader;

        Set<ServiceType> serviceTypes = new HashSet<>();
        if (StringUtils.hasText(scopeHeader)) {
            String[] scopes = scopeHeader.split(",");
            for (String scope : scopes) {
                try {
                    serviceTypes.add(ServiceType.valueOf(scope.trim()));
                } catch (IllegalArgumentException e) {
                    log.warn("Unknown ServiceType in header: {}", scope);
                }
            }
        }

        return new AdminUserProvider(userId, role, serviceTypes);


    }


}
