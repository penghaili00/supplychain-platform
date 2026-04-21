package com.supplychain.service.provider.audit.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.supplychain.common.mybatis.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 操作日志实体
 */
@Data
@TableName("sys_operation_log")
@EqualsAndHashCode(callSuper = true)
public class SysOperationLog extends BaseEntity {

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
    private String businessType;

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
     * 是否成功，1 成功，0 失败
     */
    private Integer success;

    /**
     * 附加消息
     */
    private String message;

    /**
     * 操作时间
     */
    private LocalDateTime operateTime;
}
