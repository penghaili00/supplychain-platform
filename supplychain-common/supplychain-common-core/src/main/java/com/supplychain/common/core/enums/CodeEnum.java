package com.supplychain.common.core.enums;

public interface CodeEnum {

    String getCode();

    String getDesc();

    static <E extends Enum<E> & CodeEnum> E fromCode(Class<E> enumType, String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        for (E value : enumType.getEnumConstants()) {
            if (value.getCode().equalsIgnoreCase(code)) {
                return value;
            }
        }
        return null;
    }
}
