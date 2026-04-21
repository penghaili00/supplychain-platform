package com.supplychain.common.web.filter;

import com.supplychain.common.core.constant.SupplyChainConstants;
import com.supplychain.common.core.domain.SessionUser;
import com.supplychain.common.core.enums.DataScopeType;
import com.supplychain.common.core.enums.UserType;
import com.supplychain.common.security.context.UserContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class UserContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            SessionUser sessionUser = buildSessionUser(request);
            if (sessionUser != null) {
                UserContextHolder.setUser(sessionUser);
            }
            filterChain.doFilter(request, response);
        } finally {
            UserContextHolder.clear();
        }
    }

    private SessionUser buildSessionUser(HttpServletRequest request) {
        String userId = request.getHeader(SupplyChainConstants.HEADER_USER_ID);
        if (!StringUtils.hasText(userId)) {
            return null;
        }
        return SessionUser.builder()
                .userId(Long.parseLong(userId))
                .username(request.getHeader(SupplyChainConstants.HEADER_USERNAME))
                .displayName(request.getHeader(SupplyChainConstants.HEADER_DISPLAY_NAME))
                .userType(UserType.valueOf(request.getHeader(SupplyChainConstants.HEADER_USER_TYPE)))
                .deptId(parseLong(request.getHeader(SupplyChainConstants.HEADER_DEPT_ID)))
                .sessionId(request.getHeader(SupplyChainConstants.HEADER_SESSION_ID))
                .dataScopeType(DataScopeType.fromCode(request.getHeader(SupplyChainConstants.HEADER_DATA_SCOPE)))
                .permissions(split(request.getHeader(SupplyChainConstants.HEADER_PERMISSIONS)))
                .roles(split(request.getHeader(SupplyChainConstants.HEADER_ROLES)))
                .build();
    }

    private Long parseLong(String value) {
        return StringUtils.hasText(value) ? Long.parseLong(value) : null;
    }

    private List<String> split(String value) {
        if (!StringUtils.hasText(value)) {
            return Collections.emptyList();
        }
        return Arrays.stream(value.split(","))
                .filter(StringUtils::hasText)
                .toList();
    }
}
