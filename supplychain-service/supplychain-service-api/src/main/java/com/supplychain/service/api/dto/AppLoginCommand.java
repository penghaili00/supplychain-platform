package com.supplychain.service.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * App 端登录请求
 */
@Data
public class AppLoginCommand implements Serializable {

    /**
     * 登录账号
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 登录密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 请求时间戳，单位毫秒
     */
    @NotNull(message = "timestamp 不能为空")
    private Long timestamp;

    /**
     * 随机串，用于防重放
     */
    @NotBlank(message = "nonce 不能为空")
    private String nonce;

    /**
     * 登录签名
     */
    @NotBlank(message = "signature 不能为空")
    private String signature;

    /**
     * 客户端 IP，由服务端回填
     */
    private String clientIp;
}
