package com.shixun.controller.vo;
import lombok.Data;
import java.util.List;
/**
 * 预测数据值对象，封装时间序列预测的相关数据
 */
@Data
public class ForecastVO {
    /**
     * 时间序列数据的X轴坐标值，通常表示年份
     * 例如：[2020, 2021, 2022, 2023, 2024, 2025]
     */
    private List<Integer> x;

    /**
     * 时间序列数据的Y轴坐标值，表示历史观测值
     * 对于就业率预测，单位为百分比(%)
     * 对于薪资预测，单位为人民币元
     */
    private List<Double> y;

    /**
     * 预测值数组，包含未来的预测结果
     * 数据维度通常小于等于历史观测值
     */
    private List<Double> forecast;
}