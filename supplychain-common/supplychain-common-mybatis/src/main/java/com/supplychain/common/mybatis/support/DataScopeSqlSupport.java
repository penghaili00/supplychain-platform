package com.supplychain.common.mybatis.support;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.supplychain.common.core.domain.SessionUser;
import com.supplychain.common.core.enums.DataScopeType;
import org.springframework.stereotype.Component;

@Component
public class DataScopeSqlSupport {

    public <T> LambdaQueryWrapper<T> apply(LambdaQueryWrapper<T> wrapper,
                                           SessionUser sessionUser,
                                           SFunction<T, ?> deptColumn,
                                           SFunction<T, ?> userIdColumn,
                                           SFunction<T, ?> ancestorsColumn) {
        if (sessionUser == null || sessionUser.isAdmin()) {
            return wrapper;
        }
        DataScopeType dataScopeType = sessionUser.getDataScopeType();
        if (dataScopeType == null) {
            return wrapper.eq(userIdColumn, sessionUser.getUserId());
        }
        return switch (dataScopeType) {
            case ALL -> wrapper;
            case DEPT -> wrapper.eq(deptColumn, sessionUser.getDeptId());
            case DEPT_AND_CHILD -> applyDeptAndChild(wrapper, sessionUser, deptColumn, ancestorsColumn);
            case SELF -> wrapper.eq(userIdColumn, sessionUser.getUserId());
        };
    }

    private <T> LambdaQueryWrapper<T> applyDeptAndChild(LambdaQueryWrapper<T> wrapper,
                                                        SessionUser sessionUser,
                                                        SFunction<T, ?> deptColumn,
                                                        SFunction<T, ?> ancestorsColumn) {
        if (ancestorsColumn == null) {
            return wrapper.eq(deptColumn, sessionUser.getDeptId());
        }
        return wrapper.and(q -> q.eq(deptColumn, sessionUser.getDeptId())
                .or()
                .like(ancestorsColumn, "," + sessionUser.getDeptId() + ","));
    }
}
