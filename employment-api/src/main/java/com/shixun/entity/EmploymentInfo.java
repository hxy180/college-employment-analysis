package com.shixun.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * (EmploymentInfo)表实体类
 *
 * @author makejava
 * @since 2025-06-30 18:45:10
 */

@Data
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("serial")
public class EmploymentInfo extends Model<EmploymentInfo> {

    // 就业记录唯一标识
    @TableId(value = "employment_id", type = IdType.AUTO)  // 这里配置表中主键字段名和主键策略
    private Integer employmentId;
    // 对应学生ID
    private Integer studentId;
    // 是否就业
    private String isEmployed;
    // 就职公司ID
    private Integer companyId;
    // 就业岗位名称
    private String jobTitle;
    // 就业时间
    private Date employmentDate;

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    public Serializable pkVal() {
        return this.employmentId;
    }
}

