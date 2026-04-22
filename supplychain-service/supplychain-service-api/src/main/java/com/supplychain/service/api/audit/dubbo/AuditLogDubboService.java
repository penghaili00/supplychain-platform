package com.supplychain.service.api.audit.dubbo;

import com.supplychain.service.api.audit.command.AuditLogCommand;

public interface AuditLogDubboService {

    void save(AuditLogCommand command);
}
