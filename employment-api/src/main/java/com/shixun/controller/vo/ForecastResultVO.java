package com.shixun.controller.vo;
import lombok.Data;
/**
 * 预测结果值对象，封装完整的预测分析结果
 */
@Data
public class ForecastResultVO {
    /**
     * 预测数据主体，包含时间序列的历史数据和预测数据
     */
    private ForecastVO data;

    /**
     * 预测结果的文本摘要，包含对预测趋势的解释和关键结论
     * 例如："根据历史数据和当前经济形势预测，未来两年就业率将持续增长..."
     */
    private String summary;

    /**
     * 预测结果的置信度，取值范围为0-100
     * 表示预测结果的可靠程度，值越高表示可靠性越高
     */
    private Integer confidence;
}