package com.shixun.controller.dto;

import lombok.Data;

@Data
public class RegionTopDTO {
    private String region; // 地区名称（省份/城市）
    private Integer employment; // 就业人数（仅topEmployment有值）
    private Double salary; // 平均薪资（仅topSalary有值）
    private Double growth; // 增长率（薪资/就业）
}
