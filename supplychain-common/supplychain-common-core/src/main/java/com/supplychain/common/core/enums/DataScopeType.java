package com.supplychain.common.core.enums;

public enum DataScopeType {
    ALL,
    DEPT,
    DEPT_AND_CHILD,
    SELF;

    public static DataScopeType fromCode(String code) {
        for (DataScopeType value : values()) {
            if (value.name().equalsIgnoreCase(code)) {
                return value;
            }
        }
        return SELF;
    }
}
