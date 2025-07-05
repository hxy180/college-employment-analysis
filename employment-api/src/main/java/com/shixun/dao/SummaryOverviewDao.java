package com.shixun.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shixun.controller.vo.educationSalariesVO;
import com.shixun.entity.SummaryOverview;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * (SummaryOverview)表数据库访问层
 *
 * @author makejava
 * @since 2025-06-30 18:48:50
 */
public interface SummaryOverviewDao extends BaseMapper<SummaryOverview> {
    /**
     * 查询最新统计日期的概览数据
     *
     * @return 最新统计日期的概览数据列表
     */
    @Select("SELECT * FROM summary_overview WHERE stat_date = (SELECT MAX(stat_date) FROM summary_overview)")
    List<SummaryOverview> selectLatestSummary();

    /**
     * 查询最近3年的概览数据
     *
     * @return 近3年的概览数据列表（基于当前日期计算）
     */
    @Select("SELECT * FROM summary_overview WHERE stat_date >= DATE_SUB(CURDATE(), INTERVAL 3 YEAR)")
    List<SummaryOverview> selectRecentYearsSummary();

    default List<SummaryOverview> getEducationSalaryRange(){
        return   selectLatestSummary();
    };

    default List<SummaryOverview> getEducationRate(){
        return selectLatestSummary();
    }
}

