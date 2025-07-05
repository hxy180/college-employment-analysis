package com.shixun.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shixun.entity.SummaryMajor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * (SummaryMajor)表数据库访问层
 *
 * @author makejava
 * @since 2025-06-30 18:48:35
 */
@Mapper
public interface SummaryMajorDao extends BaseMapper<SummaryMajor> {
    //----------工具
    // 获取最新统计日期
     default SummaryMajor getMapperLatestStatDate() {
         return this.selectOne(
                new LambdaQueryWrapper<SummaryMajor>()
                        .select(SummaryMajor::getStatDate)
                        .orderByDesc(SummaryMajor::getStatDate)
                        .last("LIMIT 1")
        );
    }

     default Page<SummaryMajor> getMapperTableData(Integer pageNum, Integer pageSize, String majorName, String topIndustry) {

         SummaryMajor mapperLatestStatDate = getMapperLatestStatDate();
         // 分页查询
        Page<SummaryMajor> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SummaryMajor> queryWrapper = new LambdaQueryWrapper<SummaryMajor>()
                .eq(SummaryMajor::getStatDate, mapperLatestStatDate!= null ? mapperLatestStatDate.getStatDate() : null)
                .like(StringUtils.isNotBlank(majorName), SummaryMajor::getMajorName, majorName)
                .like(StringUtils.isNotBlank(topIndustry), SummaryMajor::getTopIndustry, topIndustry)
                .orderByDesc(SummaryMajor::getEmploymentRate);
         return this.selectPage(page, queryWrapper);
     }


     default List<SummaryMajor> getMapperSalaryData(Integer limit) {
         SummaryMajor mapperLatestStatDate = getMapperLatestStatDate();
        return this.selectList(
                        new LambdaQueryWrapper<SummaryMajor>()
                                .eq(SummaryMajor::getStatDate, mapperLatestStatDate!= null ? mapperLatestStatDate.getStatDate() : null)
                                .orderByDesc(SummaryMajor::getAvgSalary)
                                .last("LIMIT " + limit)
                );
    }


     default List<SummaryMajor> getMapperBottom10ByEmploymentRate() {
         SummaryMajor mapperLatestStatDate = getMapperLatestStatDate();
         return this.selectList(
                        new LambdaQueryWrapper<SummaryMajor>()
                                .eq(SummaryMajor::getStatDate, mapperLatestStatDate!= null ? mapperLatestStatDate.getStatDate() : null)
                                .orderByAsc(SummaryMajor::getEmploymentRate)
                                .last("LIMIT 10")
                );
    }

    default List<SummaryMajor> getMapperTop10ByEmploymentRate() {
        SummaryMajor mapperLatestStatDate = getMapperLatestStatDate();
        return this.selectList(

                        new LambdaQueryWrapper<SummaryMajor>()
                                .eq(SummaryMajor::getStatDate, mapperLatestStatDate!= null ? mapperLatestStatDate.getStatDate() : null)
                                .orderByDesc(SummaryMajor::getEmploymentRate)
                                .last("LIMIT 10")
                );
    }


    default List<SummaryMajor> getMapperTop10ByIndustrySalary() {
        SummaryMajor mapperLatestStatDate = getMapperLatestStatDate();
        return this.selectList(

                        new LambdaQueryWrapper<SummaryMajor>()
                                .eq(SummaryMajor::getStatDate, mapperLatestStatDate!= null ? mapperLatestStatDate.getStatDate() : null)
                                .orderByDesc(SummaryMajor::getAvgSalary)
                                .last("LIMIT 10")
                );
    }

    default List<SummaryMajor> getMapperBottom10ByIndustrySalary() {
        SummaryMajor mapperLatestStatDate = getMapperLatestStatDate();
        return this.selectList(
                new LambdaQueryWrapper<SummaryMajor>()
                        .eq(SummaryMajor::getStatDate, mapperLatestStatDate!= null ? mapperLatestStatDate.getStatDate() : null)
                        .orderByAsc(SummaryMajor::getAvgSalary)
                        .last("LIMIT 10")
        );
    }

    default List<SummaryMajor> getAllByIndustrySalary() {
        SummaryMajor mapperLatestStatDate = getMapperLatestStatDate();
        return this.selectList(
                new LambdaQueryWrapper<SummaryMajor>()
                        .eq(SummaryMajor::getStatDate, mapperLatestStatDate!= null ? mapperLatestStatDate.getStatDate() : null)
                        .orderByDesc(SummaryMajor::getAvgSalary)
        );
    }


    @Select("SELECT SUM(employed_students) FROM summary_major WHERE YEAR(stat_date) = #{year}")
    Integer getTotalEmploymentByYear(@Param("year") Date year);
}

