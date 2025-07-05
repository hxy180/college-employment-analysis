package com.shixun.controller.vo;

import com.shixun.controller.dto.RegionGrowthDTO;
import com.shixun.controller.dto.RegionTopDTO;
import lombok.Data;
import java.util.List;

/**
 * 地区维度统计聚合VO（前端整体数据入口）
 */
@Data
public class RegionStatisticsVO {
    // 就业人数TOP1地区（对应前端 topEmployment）
    private RegionTopDTO topEmployment;
    // 平均薪资TOP1地区（对应前端 topSalary）
    private RegionTopDTO topSalary;
    // 增长最快地区（对应前端 topGrowth）(增长率top)
    private List<RegionGrowthDTO> topGrowth;
    // 最新统计时间（对应 stat_date）
    private String latestStatDate;
}