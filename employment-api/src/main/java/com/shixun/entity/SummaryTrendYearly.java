package com.shixun.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * (SummaryTrendYearly)表实体类
 *
 * @author makejava
 * @since 2025-06-30 18:49:47
 */

@Data
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("serial")
public class SummaryTrendYearly extends Model<SummaryTrendYearly> {

    // 年份
    private Integer statYear;
    // 总毕业人数
    private Integer totalStudents;
    // 已就业人数
    private Integer employedStudents;
    // 就业率
    private Float employmentRate;
    // 平均薪资
    private Double avgSalary;
    //中位数薪资
    private Double midSalary;
    //是否应届
    private Integer gradCode;

}

