package com.shixun.controller.dto;

import lombok.Data;

@Data
public class MapDataDTO {
    private String name;   // 省份名
    private Integer value; // 就业人数
    // 平均薪资（用于薪资地图，对应 avg_salary）
    private Double salary;
}
