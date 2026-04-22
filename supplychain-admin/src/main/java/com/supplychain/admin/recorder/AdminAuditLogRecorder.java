package com.supplychain.admin.recorder;

import com.supplychain.common.core.domain.AuditLogRecord;
import com.supplychain.common.core.spi.AuditLogRecorder;
import com.supplychain.service.api.audit.dubbo.AuditLogDubboService;
import com.supplychain.service.api.audit.command.AuditLogCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AdminAuditLogRecorder implements AuditLogRecorder {

    @DubboReference(check = false)
    private AuditLogDubboService auditLogDubboService;

    @Override
    public void record(AuditLogRecord auditLogRecord) {
        try {
            auditLogDubboService.save(AuditLogCommand.builder()
                    .operatorId(auditLogRecord.getOperatorId())
                    .operatorName(auditLogRecord.getOperatorName())
                    .title(auditLogRecord.getTitle())
                    .businessType(auditLogRecord.getBusinessType())
                    .method(auditLogRecord.getMethod())
                    .requestUri(auditLogRecord.getRequestUri())
                    .requestMethod(auditLogRecord.getRequestMethod())
                    .ip(auditLogRecord.getIp())
                    .requestParam(auditLogRecord.getRequestParam())
                    .responseData(auditLogRecord.getResponseData())
                    .success(auditLogRecord.getSuccess())
                    .message(auditLogRecord.getMessage())
                    .operateTime(auditLogRecord.getOperateTime())
                    .build());
        } catch (Exception exception) {
            log.warn("Persist admin audit log failed", exception);
        }
    }
}
