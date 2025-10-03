package com.example.springboot.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应结果类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    /**
     * 总记录数
     */
    private long total;

    /**
     * 当前页码
     */
    private int pageNum;

    /**
     * 每页条数
     */
    private int pageSize;

    /**
     * 分页数据列表
     */
    private List<T> list;

    /**
     * 构建分页结果
     */
    public static <T> PageResult<T> build(List<T> list, long total, int pageNum, int pageSize) {
        return new PageResult<>(total, pageNum, pageSize, list);
    }
}