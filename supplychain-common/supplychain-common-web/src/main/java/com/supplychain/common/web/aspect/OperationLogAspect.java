package com.supplychain.common.web.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplychain.common.core.annotation.OperationLog;
import com.supplychain.common.core.domain.AuditLogRecord;
import com.supplychain.common.core.domain.SessionUser;
import com.supplychain.common.core.spi.AuditLogRecorder;
import com.supplychain.common.security.context.UserContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final ObjectMapper objectMapper;
    private final ObjectProvider<AuditLogRecorder> auditLogRecorderProvider;

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
        HttpServletRequest request = currentRequest();
        SessionUser sessionUser = UserContextHolder.getUser();
        String requestBody = operationLog.saveRequestData() ? toJson(joinPoint.getArgs()) : null;
        Object result = null;
        Throwable throwable = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable ex) {
            throwable = ex;
            throw ex;
        } finally {
            AuditLogRecorder recorder = auditLogRecorderProvider.getIfAvailable();
            if (recorder != null) {
                recorder.record(AuditLogRecord.builder()
                        .operatorId(sessionUser == null ? null : sessionUser.getUserId())
                        .operatorName(sessionUser == null ? "anonymous" : sessionUser.getDisplayName())
                        .title(operationLog.title())
                        .businessType(operationLog.businessType())
                        .method(MethodSignature.class.cast(joinPoint.getSignature()).toShortString())
                        .requestUri(request == null ? null : request.getRequestURI())
                        .requestMethod(request == null ? null : request.getMethod())
                        .ip(request == null ? null : request.getRemoteAddr())
                        .requestParam(requestBody)
                        .responseData(operationLog.saveResponseData() ? toJson(result) : null)
                        .success(throwable == null)
                        .message(throwable == null ? "success" : throwable.getMessage())
                        .operateTime(LocalDateTime.now())
                        .build());
            }
        }
    }

    private HttpServletRequest currentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            log.warn("Serialize operation log payload failed", exception);
            return String.valueOf(value);
        }
    }
}
