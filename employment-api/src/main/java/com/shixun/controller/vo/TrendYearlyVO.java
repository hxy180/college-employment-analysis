package com.shixun.controller.vo;

import lombok.Data;

/**
 * 年度趋势分析视图对象
 */
@Data
public class TrendYearlyVO {
    /**
     * 年份
     */
    private Integer currentYear;
    /**
     * 就业率（百分比）
     */
    private Double currentEmploymentRate;
    /**
     * 平均薪资
     */
    private Double currentAvgSalary;
    /**
     * 中位数薪资
     */
    private Double currentMidSalary;
}
