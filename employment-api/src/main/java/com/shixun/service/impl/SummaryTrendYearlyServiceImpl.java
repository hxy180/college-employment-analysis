package com.shixun.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shixun.controller.vo.ForecastResultVO;
import com.shixun.controller.vo.ForecastVO;
import com.shixun.controller.vo.TrendYearlyVO;
import com.shixun.convert.TrendYearlyConvert;
import com.shixun.dao.SummaryTrendYearlyDao;
import com.shixun.entity.SummaryTrendYearly;
import com.shixun.utils.BusinessException;
import com.shixun.utils.ErrorCode;
import com.shixun.utils.JavaRegressionPredictor;
import com.shixun.service.SummaryTrendYearlyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smile.data.DataFrame;
import smile.data.formula.Formula;
import smile.regression.LinearModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * (SummaryTrendYearly)表服务实现类
 *
 * @author makejava
 * @since 2025-06-30 18:49:47
 */
@Slf4j
@Service("summaryTrendYearlyService")
public class SummaryTrendYearlyServiceImpl extends ServiceImpl<SummaryTrendYearlyDao, SummaryTrendYearly> implements SummaryTrendYearlyService {


    @Autowired
    SummaryTrendYearlyDao summaryTrendYearlyDao;
    @Override
    public List<TrendYearlyVO> getRecentTrends(int date, int gradCode) {
        List<SummaryTrendYearly> recentDateYear = summaryTrendYearlyDao.getRecentDateYear(date, gradCode);// 升序排序
        return TrendYearlyConvert.INSTANCE.toTrendYearlyVOList(recentDateYear);
    }

    @Override
    public ForecastResultVO getRecentForecast(String type) {

        //1.获取5年所有就业汇总数据
        List<SummaryTrendYearly> list = summaryTrendYearlyDao.getRecentDateYear(5, 0);
        // 2. 封装 ForecastVO
        List<Integer> x = list.stream().map(SummaryTrendYearly::getStatYear).collect(Collectors.toList());
        List<Double> y;
        if ("Employment".equals(type)){
             y = list.stream().map(e -> e.getEmploymentRate().doubleValue()).collect(Collectors.toList());
        } else if ("Salary".equals(type)) {
            y = list.stream().map(SummaryTrendYearly::getAvgSalary).collect(Collectors.toList());
        }else {
            log.info("请传入预测参数forecast:{}",type);
            throw new BusinessException(ErrorCode.DATA_NOT_EMPTY, "请传入预测参数forecast: Employment或Salary");
        }
        // 3. 简单预测逻辑（接入真实模型）
        List<Double> predictions = JavaRegressionPredictor.predict(x, y,3);
        //4. 直接传入模型计算置信度
        DataFrame data = JavaRegressionPredictor.buildDataFrame(x, y);
        LinearModel model = smile.regression.OLS.fit(Formula.lhs("y"), data);
        int confidence = JavaRegressionPredictor.calculateConfidence(x, y, model);
        //5.构造ForecastVO
        ForecastVO forecastVO = new ForecastVO();
        forecastVO.setX(x);
        forecastVO.setY(y);
        log.info("预测列表数据{}",predictions);
        forecastVO.setForecast(predictions);
        // 6. 构造 ForecastResultVO
        ForecastResultVO result = new ForecastResultVO();
        result.setData(forecastVO);
        result.setSummary("基于历史就业率趋势进行简单预测，未来两年就业率预计继续稳步上升。");
        result.setConfidence(confidence); // 静态置信度或你可计算
        return result;
    }

}

