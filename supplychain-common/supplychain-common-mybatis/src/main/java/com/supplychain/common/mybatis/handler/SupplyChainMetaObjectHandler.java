package com.supplychain.common.mybatis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.supplychain.common.core.domain.SessionUser;
import com.supplychain.common.security.context.UserContextHolder;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SupplyChainMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        this.strictInsertFill(metaObject, "createdTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updatedTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "createdBy", String.class, currentOperator());
        this.strictInsertFill(metaObject, "updatedBy", String.class, currentOperator());
        this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updatedTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updatedBy", String.class, currentOperator());
    }

    private String currentOperator() {
        SessionUser sessionUser = UserContextHolder.getUser();
        return sessionUser == null ? "system" : sessionUser.getUsername();
    }
}
