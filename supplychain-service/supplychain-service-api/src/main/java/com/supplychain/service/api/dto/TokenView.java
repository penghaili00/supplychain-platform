package com.supplychain.service.api.dto;

import com.supplychain.common.core.domain.SessionUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录令牌响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenView implements Serializable {

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 访问令牌过期时间
     */
    private LocalDateTime accessExpireAt;

    /**
     * 刷新令牌过期时间
     */
    private LocalDateTime refreshExpireAt;

    /**
     * 当前登录用户会话
     */
    private SessionUser user;
}
