package com.supplychain.service.api.auth.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 刷新令牌请求
 */
@Data
public class RefreshTokenCommand implements Serializable {

    /**
     * 刷新令牌
     */
    @NotBlank(message = "refreshToken 不能为空")
    private String refreshToken;
}
