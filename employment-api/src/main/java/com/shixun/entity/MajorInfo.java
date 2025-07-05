package com.shixun.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * (MajorInfo)表实体类
 *
 * @author makejava
 * @since 2025-06-30 18:45:47
 */

@Data
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("serial")
public class MajorInfo extends Model<MajorInfo> {

    // 专业唯一标识
    @TableId(value = "major_id", type = IdType.AUTO)  // 这里配置表中主键字段名和主键策略
    private Integer majorId;
    // 专业名称
    private String majorName;
    // 所属学院
    private String department;

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    public Serializable pkVal() {
        return this.majorId;
    }
}

