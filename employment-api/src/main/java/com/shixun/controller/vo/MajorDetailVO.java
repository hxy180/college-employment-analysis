package com.shixun.controller.vo;

import lombok.Data;

@Data
public class MajorDetailVO {
    /** 专业名称 */
    private String major;

    /** 就业率（百分比） */
    private Double employmentRate;

    /** 平均薪资（元） */
    private Integer avgSalary;

    /** 就业人数 */
    private Integer employmentCount;

    /** 主要就业行业 */
    private String topIndustry;

    /** 该行业就业占比（百分比，如35.2） */
    private Double industryPercent;
}