package com.supplychain.common.core.domain;

import com.supplychain.common.core.enums.OperationType;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class AuditLogRecord implements Serializable {

    private Long operatorId;
    private String operatorName;
    private String title;
    private OperationType businessType;
    private String method;
    private String requestUri;
    private String requestMethod;
    private String ip;
    private String requestParam;
    private String responseData;
    private Boolean success;
    private String message;
    private LocalDateTime operateTime;
}
