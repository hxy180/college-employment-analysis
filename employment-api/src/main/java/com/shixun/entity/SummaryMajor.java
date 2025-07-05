package com.shixun.entity;

import java.util.Date;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * (SummaryMajor)表实体类
 *
 * @author makejava
 * @since 2025-06-30 18:48:35
 */

@Data
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("serial")
public class SummaryMajor extends Model<SummaryMajor> {

    // 专业名称
    private String majorName;
    // 专业学生数
    private Integer totalStudents;
    // 就业人数
    private Integer employedStudents;
    // 就业率
    private Float employmentRate;
    // 平均薪资
    private Double avgSalary;
    // 最主要就业行业
    private String topIndustry;
    // 统计时间
    private Date statDate;

}

