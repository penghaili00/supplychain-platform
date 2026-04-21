package com.supplychain.common.security.context;

import com.supplychain.common.core.domain.SessionUser;

public final class UserContextHolder {

    private static final ThreadLocal<SessionUser> HOLDER = new ThreadLocal<>();

    private UserContextHolder() {
    }

    public static void setUser(SessionUser user) {
        HOLDER.set(user);
    }

    public static SessionUser getUser() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
