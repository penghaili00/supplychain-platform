package com.supplychain.common.core.annotation;

import com.supplychain.common.core.enums.OperationType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    String title();

    OperationType businessType() default OperationType.OTHER;

    boolean saveRequestData() default true;

    boolean saveResponseData() default false;
}
