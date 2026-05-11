package com.oa.system.common;

import lombok.Data;

/**
 * Page Request
 */
@Data
public class PageDTO<T> {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String orderBy;
    private String sortOrder = "desc";
    
    /**
     * 查询参数
     */
    private T params;
}
