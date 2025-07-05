package com.shixun.controller.vo;

import lombok.Data;

import java.util.Date;
@Data
public class EducationRateVO {

    // 学历（专科/本科/硕士/博士）
    private String educationLevel;
    // 该学历总学生数
    private Integer totalLevelStudents;

    private Double rate;
    // 统计时间
    private Date statDate;
}
