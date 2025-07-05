package com.shixun.convert;

import com.shixun.controller.vo.TrendYearlyVO;
import com.shixun.entity.SummaryTrendYearly;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 年度趋势数据映射器
 */
@Mapper(componentModel = "spring")
public interface TrendYearlyConvert {
    // 获取映射器实例
    TrendYearlyConvert INSTANCE = Mappers.getMapper(TrendYearlyConvert.class);

    /**
     * 将实体转换为VO
     */
    @Mapping(source = "statYear", target = "currentYear")
    @Mapping(source = "employmentRate", target = "currentEmploymentRate")
    @Mapping(source = "avgSalary", target = "currentAvgSalary")
    @Mapping(source = "midSalary", target = "currentMidSalary")
    TrendYearlyVO toVO(SummaryTrendYearly entity);
    /**
     * 将实体列表转换为VO列表
     */
    List<TrendYearlyVO> toTrendYearlyVOList(List<SummaryTrendYearly> entities);
}