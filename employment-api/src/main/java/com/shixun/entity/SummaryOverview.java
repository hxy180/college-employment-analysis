package com.shixun.entity;

import java.util.Date;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * (SummaryOverview)表实体类
 *
 * @author makejava
 * @since 2025-06-30 18:48:50
 */

@Data
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("serial")
public class SummaryOverview extends Model<SummaryOverview> {

    // 学历（专科/本科/硕士/博士）
    private String educationLevel;
    // 总学生数
    private Integer totalStudents;
    // 已就业人数
    private Integer employedStudents;
    // 就业率
    private Float employmentRate;
    // 平均薪资
    private Double avgSalary;
    // 统计时间
    private Date statDate;

}

