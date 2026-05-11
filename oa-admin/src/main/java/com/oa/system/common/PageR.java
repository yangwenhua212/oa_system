package com.oa.system.common;

import lombok.Data;
import java.util.List;

/**
 * Page Response
 */
@Data
public class PageR<T> {
    private Long total;
    private List<T> list;
    
    // 别名，与 list 相同，支持 setRecords
    private List<T> records;

    public static <T> PageR<T> ok(Long total, List<T> list) {
        PageR<T> page = new PageR<>();
        page.setTotal(total);
        page.setList(list);
        page.setRecords(list);
        return page;
    }

    public static <T> PageR<T> ok(List<T> list, Long total) {
        return ok(total, list);
    }

    public void setRecords(List<T> records) {
        this.records = records;
        this.list = records;
    }
}
