package com.shixun.convert;

import com.shixun.controller.vo.MajorDetailVO;
import com.shixun.controller.vo.MajorEmploymentVO;
import com.shixun.controller.vo.MajorSalaryVO;
import com.shixun.entity.SummaryMajor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 专业数据转换接口（MapStruct 自动生成实现类）
 */
@Mapper(componentModel = "spring") // componentModel = "spring" 表示生成 Spring 可注入的 Bean
public interface MajorMapperConvert {

    // 单例实例（方便非 Spring 环境使用，可选）
    MajorMapperConvert INSTANCE = Mappers.getMapper(MajorMapperConvert.class);

    /**
     * 将 SummaryMajor 转换为 MajorEmploymentVO
     * 映射规则：majorName → major，employmentRate 直接映射
     */
    @Mapping(source = "majorName", target = "major")
    MajorEmploymentVO toEmploymentVO(SummaryMajor summaryMajor);

    /**
     * 批量转换（用于列表）
     */
    List<MajorEmploymentVO> toEmploymentVOList(List<SummaryMajor> summaryMajorList);

    /**
     * 将 SummaryMajor 转换为 MajorSalaryVO
     * 映射规则：majorName → major，avgSalary 直接映射
     */
    @Mapping(source = "majorName", target = "major")
    MajorSalaryVO toSalaryVO(SummaryMajor summaryMajor);

    /**
     * 批量转换（用于列表）
     */
    List<MajorSalaryVO> toSalaryVOList(List<SummaryMajor> summaryMajorList);

    /**
     * 将 SummaryMajor 转换为 MajorDetailVO
     * 映射规则：
     * - majorName → major
     * - employedStudents → employmentCount
     * - 其他字段直接映射
     * - industryPercent 通过自定义方法计算
     */
    @Mapping(source = "majorName", target = "major")
    @Mapping(source = "employedStudents", target = "employmentCount")
    @Mapping(source = ".", target = "industryPercent", qualifiedByName = "calculateIndustryPercent")
    MajorDetailVO toDetailVO(SummaryMajor summaryMajor);

    /**
     * 批量转换（用于列表）
     */
    List<MajorDetailVO> toDetailVOList(List<SummaryMajor> summaryMajorList);

    /**
     * 自定义方法：计算行业占比（就业人数 / 总人数）
     */
    @Named("calculateIndustryPercent")
    default Double calculateIndustryPercent(SummaryMajor summaryMajor) {
        if (summaryMajor.getTotalStudents() == 0) {
            return 0.0;
        }
        // 保留一位小数
        return Math.round(
                (summaryMajor.getEmployedStudents() * 100.0 / summaryMajor.getTotalStudents()) * 10
        ) / 10.0;
    }
}