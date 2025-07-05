package com.shixun.controller.vo;

import lombok.Data;
import java.util.List;

/**
 * 城市维度表格数据VO（优化前端展示）
 */
import lombok.Data;
import java.util.List;

/**
 * 城市维度表格数据VO（简化版，仅保留原始数据和必要展示字段）
 */
@Data
public class CityTableVO {
    // 基础信息
    private String region;             // 城市名称
    private String province;           // 所属省份
    private String regionFullName;     // 完整名称（省份-城市）

    // 就业数据
    private Integer employment;        // 就业人数

    // 薪资数据
    private Integer avgSalary;         // 平均薪资

    // 增长率数据
    private Double salaryGrowth;       // 薪资增长率（如5.2%存储为5.2）

    // 行业分布
    private List<String> industryTags; // 主要行业标签列表

    // 其他数据
    private Double graduateRatio;     // 毕业生占比（如65%存储为65）
}