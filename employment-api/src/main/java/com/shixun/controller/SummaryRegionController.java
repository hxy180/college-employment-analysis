package com.shixun.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shixun.controller.dto.MapDataDTO;
import com.shixun.controller.vo.CityTableVO;
import com.shixun.controller.vo.MajorDetailVO;
import com.shixun.controller.vo.RegionStatisticsVO;
import com.shixun.utils.PageResult;
import com.shixun.utils.Result;

import static com.shixun.utils.Result.success;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shixun.entity.SummaryRegion;
import com.shixun.service.SummaryRegionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * (SummaryRegion)表控制层
 *
 * @author makejava
 * @since 2025-06-30 18:49:04
 */
@RestController
@RequestMapping("/api/summaryRegion")
public class SummaryRegionController {
    /**
     * 服务对象
     */
    @Resource
    private SummaryRegionService summaryRegionService;

    /**
     * 返回地图就业人数/平均薪资
     * @return
     */
    @GetMapping("/mapData")
    public Result<?> getRegionEmploymentMapData() {
        List<MapDataDTO> regionEmploymentMapData = summaryRegionService.getRegionEmploymentMapData();
        return Result.success(regionEmploymentMapData);
    }


    /**
     * 获取地区就业统计 TopN 数据（人数、薪资、增长率）
     * @param topN 需要返回的前N个地区
     * @return Result封装的统计数据列表
     */
    @GetMapping("/topList")
    public Result<?> getRegionSummaryTop(@RequestParam(defaultValue = "5") int topN) {
        RegionStatisticsVO regionStatistics = summaryRegionService.getRegionStatistics(topN);
        return Result.success(regionStatistics);
    }

    /**
     * 获取地区就业统计数据（地区、人数、薪资、增长率）
     * @param pageNum
     * @param pageSize
     * @param province
     * @param region
     * @return
     */
    @GetMapping("/cityTable")
//    @ApiOperation("获取城市数据列表（分页）")
    public Result<PageResult<CityTableVO>> getCityTableData(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String province, // 按省名称筛选
            @RequestParam(required = false) String region // 按城市筛选
    ) {

        PageResult<CityTableVO> pageInfo = summaryRegionService.getCityTableData(pageNum, pageSize,province,region);
        return Result.success(pageInfo);
    }


}

