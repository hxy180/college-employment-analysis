package com.shixun.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageResult<T> {
    private long pageNum; // 当前页码
    private long pageSize; // 每页条数
    private long total; // 总记录数
    private List<T> list; // 数据列表
}