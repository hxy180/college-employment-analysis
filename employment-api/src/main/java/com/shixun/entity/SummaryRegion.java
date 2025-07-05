package com.shixun.entity;

import java.util.Date;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * (SummaryRegion)表实体类
 *
 * @author makejava
 * @since 2025-06-30 18:49:04
 */

@Data
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("serial")
public class SummaryRegion extends Model<SummaryRegion> {

    // 省份名称
    private String province;
    // 城市名称
    private String city;
    // 就业人数
    private Integer employedStudents;
    // 平均薪资
    private Double avgSalary;
    // 统计时间
    private Date statDate;

}

