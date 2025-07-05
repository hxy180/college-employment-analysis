package com.shixun.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * (RegionInfo)表实体类
 *
 * @author makejava
 * @since 2025-06-30 18:46:15
 */

@Data
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("serial")
public class RegionInfo extends Model<RegionInfo> {

    // 地区唯一标识
    @TableId(value = "region_id", type = IdType.AUTO)  // 这里配置表中主键字段名和主键策略
    private Integer regionId;
    // 省份名称
    private String province;
    // 城市名称
    private String city;

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    public Serializable pkVal() {
        return this.regionId;
    }
}

