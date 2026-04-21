package com.supplychain.service.api.dto;

import com.supplychain.common.core.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志写入命令
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogCommand implements Serializable {

    /**
     * 操作人 ID
     */
    private Long operatorId;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * 日志标题
     */
    private String title;

    /**
     * 业务类型
     */
    private OperationType businessType;

    /**
     * 请求方法签名
     */
    private String method;

    /**
     * 请求路径
     */
    private String requestUri;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 请求 IP
     */
    private String ip;

    /**
     * 请求参数
     */
    private String requestParam;

    /**
     * 响应结果
     */
    private String responseData;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 日志消息
     */
    private String message;

    /**
     * 操作时间
     */
    private LocalDateTime operateTime;
}
