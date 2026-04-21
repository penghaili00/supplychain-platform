package com.supplychain.service.provider.audit.service;

import com.supplychain.service.api.dto.AuditLogCommand;
import com.supplychain.service.provider.audit.entity.SysOperationLog;
import com.supplychain.service.provider.audit.mapper.SysOperationLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final SysOperationLogMapper sysOperationLogMapper;

    public void save(AuditLogCommand command) {
        SysOperationLog entity = new SysOperationLog();
        entity.setOperatorId(command.getOperatorId());
        entity.setOperatorName(command.getOperatorName());
        entity.setTitle(command.getTitle());
        entity.setBusinessType(command.getBusinessType() == null ? null : command.getBusinessType().name());
        entity.setMethod(command.getMethod());
        entity.setRequestUri(command.getRequestUri());
        entity.setRequestMethod(command.getRequestMethod());
        entity.setIp(command.getIp());
        entity.setRequestParam(command.getRequestParam());
        entity.setResponseData(command.getResponseData());
        entity.setSuccess(Boolean.TRUE.equals(command.getSuccess()) ? 1 : 0);
        entity.setMessage(command.getMessage());
        entity.setOperateTime(command.getOperateTime());
        sysOperationLogMapper.insert(entity);
    }
}
