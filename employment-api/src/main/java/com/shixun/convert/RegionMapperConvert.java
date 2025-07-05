package com.shixun.convert;
import com.shixun.controller.vo.CityTableVO;
import com.shixun.entity.SummaryRegion;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RegionMapperConvert {

    RegionMapperConvert INSTANCE = Mappers.getMapper(RegionMapperConvert.class);

    @Mapping(source = "city", target = "region")
    @Mapping(source = "province", target = "province")
    @Mapping(source = "employedStudents", target = "employment")
    @Mapping(target = "avgSalary", expression = "java(entity.getAvgSalary() != null ? entity.getAvgSalary().intValue() : null)")
    @Mapping(target = "regionFullName", ignore = true) // 手动处理
    @Mapping(target = "salaryGrowth", ignore = true)   // 后处理或手动赋值
    @Mapping(target = "industryTags", ignore = true)
    @Mapping(target = "graduateRatio", ignore = true)
    CityTableVO convertTCityTableVO(SummaryRegion entity);

    List<CityTableVO> convertTCityTableVOList(List<SummaryRegion> list);

    // 手动设置 regionFullName 字段
    @AfterMapping
    default void fillExtraFields(SummaryRegion entity, @MappingTarget CityTableVO vo) {
        if (entity.getProvince() != null && entity.getCity() != null) {
            vo.setRegionFullName(entity.getProvince() + "-" + entity.getCity());
        }
    }
}
