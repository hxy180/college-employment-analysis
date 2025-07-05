package com.shixun.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shixun.controller.vo.ForecastResultVO;
import com.shixun.controller.vo.TrendYearlyVO;
import com.shixun.entity.SummaryTrendYearly;

import java.util.List;

/**
 * (SummaryTrendYearly)表服务接口
 *
 * @author makejava
 * @since 2025-06-30 18:49:47
 */
public interface SummaryTrendYearlyService extends IService<SummaryTrendYearly> {

    /**
     * 返回最近date年的应届或全部TrendYearlyVO数据
     * @param date
     * @param gradCode
     * @return
     */
    List<TrendYearlyVO> getRecentTrends(int date, int gradCode);

    /**
     * 预测
     * @return
     */
    ForecastResultVO getRecentForecast(String type);


}

