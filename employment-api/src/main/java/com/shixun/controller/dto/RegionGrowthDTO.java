package com.shixun.controller.dto;

import lombok.Data;

@Data
public class RegionGrowthDTO {
    private String region; // 地区名称
    private Double rate; // 增长率（%）
    private Integer increase; // 新增就业人数（前端计算：employment * rate / 100）
}