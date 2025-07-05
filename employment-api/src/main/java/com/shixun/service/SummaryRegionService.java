package com.shixun.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shixun.controller.dto.MapDataDTO;
import com.shixun.controller.vo.CityTableVO;
import com.shixun.controller.vo.RegionStatisticsVO;
import com.shixun.entity.SummaryRegion;
import com.shixun.utils.PageResult;

import java.util.List;

/**
 * (SummaryRegion)表服务接口
 *
 * @author makejava
 * @since 2025-06-30 18:49:04
 */
public interface SummaryRegionService extends IService<SummaryRegion> {

    /**
     * 获取地区统计的地图数据 省份+人数/平均薪资
     * @return 地区统计聚合VO（map dataVO）
     */
    public List<MapDataDTO> getRegionEmploymentMapData();
    /**
     * 获取地区统计的完整数据（包含TOP指标、最新统计时间等）
     * @return 地区统计聚合VO（RegionStatisticsVO）
     */
    RegionStatisticsVO getRegionStatistics(int topN);


    /**
     * 根据查询条件返回cityTable集合
     * @param pageNum
     * @param pageSize
     * @param province
     * @param region
     * @return
     */
    public PageResult<CityTableVO> getCityTableData(Integer pageNum, Integer pageSize, String province, String region);
}

