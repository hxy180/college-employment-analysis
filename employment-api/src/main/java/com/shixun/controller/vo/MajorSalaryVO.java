package com.shixun.controller.vo;

import lombok.Data;

/**
 * 专业薪资统计 VO（MajorSalaryVO）
 * 用途：存储单个专业的薪资数据（对应前端salaryData）
 */
@Data
public class MajorSalaryVO {
    /** 专业名称 */
    private String major;

    /** 平均薪资（单位：元） */
    private Integer avgSalary;

    /** 薪资中位数（可选，用于更精准的薪资展示） */
    private Integer medianSalary;
}
