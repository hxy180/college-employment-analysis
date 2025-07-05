package com.shixun.controller.vo;

import com.shixun.controller.dto.EducationLevelRateDTO;
import com.shixun.controller.dto.IndustryDistributionDTO;
import com.shixun.controller.dto.MapDataDTO;
import com.shixun.controller.dto.OverviewStatsDTO;
import lombok.Data;

import java.util.List;

@Data
/**
 * 概览数据VO（视图对象），用于封装前端展示所需的各类统计概览信息
 * 整合了不同维度的数据分析结果，便于前端一次性获取并展示完整的概览面板数据
 */
public class OverviewVO {

    /**
     * 各教育水平占比数据列表
     * 例如：本科、硕士、博士等不同学历层次在总体中的占比统计
     */
    List<EducationLevelRateDTO> educationLevelRateVO;

    /**
     * 概览统计核心数据
     * 通常包含关键指标汇总，如总人数、平均薪资、就业率等核心统计值
     */
    OverviewStatsDTO overviewStatsVO;

    /**
     * 行业分布数据列表
     * 展示不同行业（如互联网、金融、教育等）的分布情况，可能包含企业数量、就业人数等信息
     */
    List<IndustryDistributionDTO> industryDistributionVO;

    /**
     * 地图数据列表
     * 用于地理维度的数据分析，如各省份/城市的相关指标分布（如就业人数、薪资水平等）
     */
    List<MapDataDTO> mapDataVO;
}