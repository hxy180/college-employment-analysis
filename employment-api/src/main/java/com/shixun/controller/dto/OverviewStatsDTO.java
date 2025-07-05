package com.shixun.controller.dto;

import lombok.Data;

@Data
public class OverviewStatsDTO {

    /**
     * 毕业生总人数
     */
    private Integer totalGraduates;

    /**
     * 就业人数
     */
    private Integer employed;

    /**
     * 就业率，计算公式为：employed / totalGraduates
     */
    private Double employmentRate;

    /**
     * 平均薪资（单位：元）
     */
    private Double avgSalary;
}