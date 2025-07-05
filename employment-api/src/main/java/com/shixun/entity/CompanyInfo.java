package com.shixun.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * (CompanyInfo)表实体类
 *
 * @author makejava
 * @since 2025-06-30 18:34:29
 */

@Data
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("serial")
public class CompanyInfo extends Model<CompanyInfo> {

    // 公司唯一标识
    @TableId(value = "company_id", type = IdType.AUTO)  // 这里配置表中主键字段名和主键策略
    private Integer companyId;
    // 公司名称
    private String companyName;
    // 所属行业
    private String industry;
    // 公司所在地地区ID
    private Integer regionId;

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    public Serializable pkVal() {
        return this.companyId;
    }
}

