package com.supplychain.common.core.constant;

public final class SupplyChainConstants {

    public static final String HEADER_TRACE_ID = "X-SupplyChain-Trace-Id";
    public static final String HEADER_USER_ID = "X-SupplyChain-User-Id";
    public static final String HEADER_USERNAME = "X-SupplyChain-Username";
    public static final String HEADER_DISPLAY_NAME = "X-SupplyChain-Display-Name";
    public static final String HEADER_USER_TYPE = "X-SupplyChain-User-Type";
    public static final String HEADER_DEPT_ID = "X-SupplyChain-Dept-Id";
    public static final String HEADER_SESSION_ID = "X-SupplyChain-Session-Id";
    public static final String HEADER_DATA_SCOPE = "X-SupplyChain-Data-Scope";
    public static final String HEADER_PERMISSIONS = "X-SupplyChain-Permissions";
    public static final String HEADER_ROLES = "X-SupplyChain-Roles";

    public static final String CLAIM_SESSION_ID = "sid";
    public static final String CLAIM_USERNAME = "username";
    public static final String CLAIM_DISPLAY_NAME = "displayName";
    public static final String CLAIM_USER_TYPE = "userType";
    public static final String CLAIM_DEPT_ID = "deptId";
    public static final String CLAIM_TOKEN_TYPE = "tokenType";

    public static final String TOKEN_TYPE_ACCESS = "access";
    public static final String TOKEN_TYPE_REFRESH = "refresh";

    public static final String REDIS_SESSION_PREFIX = "supplychain:sso:session:";
    public static final String REDIS_USER_BIND_PREFIX = "supplychain:sso:user:";

    public static final String MDC_TRACE_ID = "traceId";

    private SupplyChainConstants() {
    }
}
