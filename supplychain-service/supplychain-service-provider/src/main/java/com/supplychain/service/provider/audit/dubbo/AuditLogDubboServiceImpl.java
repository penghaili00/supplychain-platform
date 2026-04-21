package com.supplychain.service.provider.audit.dubbo;

import com.supplychain.service.api.dubbo.AuditLogDubboService;
import com.supplychain.service.api.dto.AuditLogCommand;
import com.supplychain.service.provider.audit.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
@RequiredArgsConstructor
public class AuditLogDubboServiceImpl implements AuditLogDubboService {

    private final AuditLogService auditLogService;

    @Override
    public void save(AuditLogCommand command) {
        auditLogService.save(command);
    }
}
