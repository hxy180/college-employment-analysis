package com.shixun.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * (StudentInfo)表实体类
 *
 * @author makejava
 * @since 2025-06-30 18:47:06
 */

@Data
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("serial")
public class StudentInfo extends Model<StudentInfo> {

    // 学生唯一标识
    @TableId(value = "student_id", type = IdType.AUTO)  // 这里配置表中主键字段名和主键策略
    private Integer studentId;
    // 学生姓名
    private String name;
    // 性别
    private String gender;
    // 年龄
    private Integer age;
    // 学历
    private String educationLevel;
    // 所学专业ID
    private Integer majorId;
    // 毕业年份
    private Integer graduationYear;
    // 生源所在地
    private Integer regionId;

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    public Serializable pkVal() {
        return this.studentId;
    }
}

