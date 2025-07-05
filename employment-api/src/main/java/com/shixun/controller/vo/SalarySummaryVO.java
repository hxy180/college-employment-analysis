package com.shixun.controller.vo;

import lombok.Data;

/**
 * 薪资统计摘要VO
 * 用于封装薪资相关的核心统计指标，供前端展示使用
 */
@Data
public class SalarySummaryVO {
    private Double avgSalary;         // 平均薪资
    private Double medianSalary;      // 中位数薪资
    private Double salaryGrowth;      // 薪资增长率（百分比）
    private String topIndustry;       // 最高薪行业
    private Double topIndustrySalary; // 最高薪行业平均薪资
    private Double highSalaryPercent; // 高薪人群占比（百分比）
}