package com.shixun.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shixun.controller.vo.ForecastResultVO;
import com.shixun.controller.vo.TrendYearlyVO;
import com.shixun.utils.Result;

import static com.shixun.utils.Result.success;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shixun.entity.SummaryTrendYearly;
import com.shixun.service.SummaryTrendYearlyService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * (SummaryTrendYearly)表控制层
 *
 * @author makejava
 * @since 2025-06-30 18:49:47
 */
@RestController
@RequestMapping("/api/summaryTrendYearly")
public class SummaryTrendYearlyController {
    /**
     * 服务对象
     */
    @Resource
    private SummaryTrendYearlyService summaryTrendYearlyService;

    /**
     * 返回date年内的应届生或全部的就业薪资趋势集合
     * @param date
     * @param
     * @return
     */
    @GetMapping("/recent")
    public Result<List<TrendYearlyVO>> getRecentTrends(@RequestParam int date,@RequestParam int gradCode) {
        return Result.success(summaryTrendYearlyService.getRecentTrends(date,gradCode));
    }

    /**
     *返回近5年的数据并预测接下来三年的(薪资或就业率)
     * @param forecast
     * @return
     */
    @GetMapping("/forecast")
    public Result<ForecastResultVO> getForecast(@RequestParam String forecast) {
       return Result.success(summaryTrendYearlyService.getRecentForecast(forecast));
    }
}

