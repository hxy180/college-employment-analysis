package com.shixun.entity;

import java.util.Date;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * (SummarySalaryRange)表实体类
 *
 * @author makejava
 * @since 2025-06-30 18:49:20
 */

@Data
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("serial")
public class SummarySalaryRange extends Model<SummarySalaryRange> {

    // 薪资区间
    private String salaryRange;
    // 人数
    private Integer employeeCount;
//
    private Integer gradCode;
    // 统计时间
    private Date statDate;

}

