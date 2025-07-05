package com.shixun.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * (SalaryInfo)表实体类
 *
 * @author makejava
 * @since 2025-06-30 18:46:42
 */

@Data
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("serial")
public class SalaryInfo extends Model<SalaryInfo> {

    // 薪资记录唯一标识
    @TableId(value = "salary_id", type = IdType.AUTO)  // 这里配置表中主键字段名和主键策略
    private Integer salaryId;
    // 所属就业记录ID
    private Integer employmentId;
    // 薪资数额
    private String salaryAmount;
    // 薪资类型：月薪/年薪/其他
    private String salaryType;

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    public Serializable pkVal() {
        return this.salaryId;
    }
}

