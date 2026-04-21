package com.supplychain.common.core.exception;

public class ForbiddenException extends BizException {

    public ForbiddenException(String message) {
        super(403, message);
    }
}
