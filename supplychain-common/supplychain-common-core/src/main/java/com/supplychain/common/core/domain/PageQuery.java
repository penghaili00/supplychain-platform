package com.supplychain.common.core.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询基类。
 */
@Data
public class PageQuery implements Serializable {

    /**
     * 页码，从 1 开始。
     */
    private Long pageNum = 1L;

    /**
     * 每页条数。
     */
    private Long pageSize = 20L;
}
