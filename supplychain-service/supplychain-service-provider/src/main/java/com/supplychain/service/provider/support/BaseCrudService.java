package com.supplychain.service.provider.support;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supplychain.common.core.exception.BizException;
import com.supplychain.common.mybatis.domain.BaseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 通用 CRUD 服务基类
 *
 * @param <T> 实体类型
 */
public abstract class BaseCrudService<T extends BaseEntity> {

    private final BaseMapper<T> baseMapper;
    private final String entityName;

    protected BaseCrudService(BaseMapper<T> baseMapper, String entityName) {
        this.baseMapper = baseMapper;
        this.entityName = entityName;
    }

    public T getById(Long id) {
        validateId(id, entityName + "ID");
        return baseMapper.selectById(id);
    }

    public T getRequiredById(Long id) {
        validateId(id, entityName + "ID");
        T entity = baseMapper.selectById(id);
        if (entity == null) {
            throw new BizException(404, entityName + "不存在");
        }
        return entity;
    }

    public List<T> listAll() {
        QueryWrapper<T> queryWrapper = activeQuery();
        queryWrapper.orderByAsc("id");
        return baseMapper.selectList(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public Long create(T entity) {
        validateEntity(entity);
        int rows = baseMapper.insert(entity);
        if (rows != 1) {
            throw new BizException(500, entityName + "创建失败");
        }
        return entity.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(T entity) {
        validateEntity(entity);
        validateId(entity.getId(), entityName + "ID");
        getRequiredById(entity.getId());
        int rows = baseMapper.updateById(entity);
        if (rows != 1) {
            throw new BizException(500, entityName + "更新失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        validateId(id, entityName + "ID");
        getRequiredById(id);
        int rows = baseMapper.deleteById(id);
        if (rows != 1) {
            throw new BizException(500, entityName + "删除失败");
        }
    }

    protected List<T> listByColumn(String column, Object value) {
        validateValue(value, column);
        QueryWrapper<T> queryWrapper = activeQuery();
        queryWrapper.eq(column, value).orderByAsc("id");
        return baseMapper.selectList(queryWrapper);
    }

    protected T getOneByColumn(String column, Object value) {
        validateValue(value, column);
        QueryWrapper<T> queryWrapper = activeQuery();
        queryWrapper.eq(column, value).last("limit 1");
        return baseMapper.selectOne(queryWrapper);
    }

    protected List<T> selectList(QueryWrapper<T> queryWrapper) {
        return baseMapper.selectList(queryWrapper);
    }

    protected T selectOne(QueryWrapper<T> queryWrapper) {
        return baseMapper.selectOne(queryWrapper);
    }

    protected QueryWrapper<T> activeQuery() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);
        return queryWrapper;
    }

    protected void validateId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new BizException(400, fieldName + "不能为空");
        }
    }

    protected void validateText(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new BizException(400, fieldName + "不能为空");
        }
    }

    private void validateEntity(T entity) {
        if (entity == null) {
            throw new BizException(400, entityName + "不能为空");
        }
    }

    private void validateValue(Object value, String fieldName) {
        if (value == null) {
            throw new BizException(400, fieldName + "不能为空");
        }
    }
}
