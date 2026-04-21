package com.supplychain.common.core.spi;

import com.supplychain.common.core.domain.AuditLogRecord;

public interface AuditLogRecorder {

    void record(AuditLogRecord auditLogRecord);
}
