package com.shixun.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shixun.controller.vo.TrendYearlyVO;
import com.shixun.entity.SummaryTrendYearly;

import java.time.LocalDate;
import java.util.List;

/**
 * (SummaryTrendYearly)表数据库访问层
 *
 * @author makejava
 * @since 2025-06-30 18:49:47
 */
public interface SummaryTrendYearlyDao extends BaseMapper<SummaryTrendYearly> {

    default List<SummaryTrendYearly> getDateYear(int date, int gradCode){
        return this.selectList(
                new LambdaQueryWrapper<SummaryTrendYearly>() .eq(SummaryTrendYearly::getStatYear, date)       // 筛选指定年份
                .eq(SummaryTrendYearly::getGradCode, gradCode) // 筛选gradCode相等
                .orderByAsc(SummaryTrendYearly::getStatYear)   // 按年份升序排列
        );
    }

    /**
     * 获取近n年所有记录
     * @param n
     * @param gradCode
     * @return
     */

    default List<SummaryTrendYearly> getRecentDateYear(int n, int gradCode) {
        // 获取当前年份
        int currentYear = LocalDate.now().getYear();
        // 构建查询条件：最近N年且gradCode相等
        LambdaQueryWrapper<SummaryTrendYearly> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .between(SummaryTrendYearly::getStatYear, currentYear - n + 1, currentYear)
                .eq(SummaryTrendYearly::getGradCode, gradCode)
                .orderByAsc(SummaryTrendYearly::getStatYear);
        // 查询数据
        return this.selectList(queryWrapper);
    }
}

