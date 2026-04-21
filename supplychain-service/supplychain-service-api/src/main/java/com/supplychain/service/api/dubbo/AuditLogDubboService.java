package com.supplychain.service.api.dubbo;

import com.supplychain.service.api.dto.AuditLogCommand;

public interface AuditLogDubboService {

    void save(AuditLogCommand command);
}
