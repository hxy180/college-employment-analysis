package com.shixun.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shixun.entity.SummaryMajor;
import com.shixun.entity.SummaryRegion;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * (SummaryRegion)表数据库访问层
 *
 * @author makejava
 * @since 2025-06-30 18:49:04
 */
public interface SummaryRegionDao extends BaseMapper<SummaryRegion> {

    // 获取最新统计日期
    default SummaryRegion getMapperLatestStatDate() {
        return this.selectOne(
                new LambdaQueryWrapper<SummaryRegion>()
                        .select(SummaryRegion::getStatDate)
                        .orderByDesc(SummaryRegion::getStatDate)
                        .last("LIMIT 1")
        );
    }
    //获取第二统计日
    default SummaryRegion getMapperPreviousStatDate() {

        Date currentDate = getMapperLatestStatDate().getStatDate();
        return this.selectOne(
                new LambdaQueryWrapper<SummaryRegion>()
                        .select(SummaryRegion::getStatDate)
                        .lt(SummaryRegion::getStatDate, currentDate)
                        .orderByDesc(SummaryRegion::getStatDate)
                        .last("LIMIT 1")
        );
    }

    /**
     * 查询最新的统计日期
     */
    @Select("SELECT MAX(stat_date) FROM summary_region")
    String selectMaxStatDate();

    /**
     * 根据统计日期查询地区数据
     */
    @Select("SELECT * FROM summary_region WHERE stat_date = #{statDate}")
    List<SummaryRegion> selectByStatDate(String statDate);


    /**
     *  就业人数最多的记录
     */
     default SummaryRegion selectTopEmployed( SummaryRegion latestDate){
         if(latestDate==null) {
             latestDate = getMapperLatestStatDate();
         }
         return this.selectOne( new LambdaQueryWrapper<SummaryRegion>()
                .eq(SummaryRegion::getStatDate, latestDate.getStatDate())
                .orderByDesc(SummaryRegion::getEmployedStudents)
                .last("LIMIT 1")
                );
    }

    /**
     *  就业薪资最多的记录
     */
     default SummaryRegion selectTopSalary(SummaryRegion latestDate){
         if(latestDate==null) {
             latestDate = getMapperLatestStatDate();
         }
         return this.selectOne( new LambdaQueryWrapper<SummaryRegion>()
                .eq(SummaryRegion::getStatDate, latestDate.getStatDate())
                .orderByDesc(SummaryRegion::getAvgSalary)
                .last("LIMIT 1")
                );
    }

    /**
     * 查询cityTableData
     * @param pageNum
     * @param pageSize
     * @param province
     * @param region
     * @return
     */
    default Page<SummaryRegion> getMapperTableData(Integer pageNum, Integer pageSize, String province, String region) {
        // 查询最新统计日期
        SummaryRegion latestRecord = getMapperLatestStatDate();
        // 分页查询
        Page<SummaryRegion> page = new Page<>(pageNum, pageSize);
        // 构建查询条件
        LambdaQueryWrapper<SummaryRegion> queryWrapper = new LambdaQueryWrapper<SummaryRegion>()
                // 使用最新统计日期
                .eq(SummaryRegion::getStatDate, latestRecord != null ? latestRecord.getStatDate() : null)
                // 省份条件（模糊匹配）
                .like(StringUtils.isNotBlank(province), SummaryRegion::getProvince, province)
                // 城市条件（模糊匹配）
                .like(StringUtils.isNotBlank(region), SummaryRegion::getCity, region)
                // 按就业人数降序排序
                .orderByDesc(SummaryRegion::getEmployedStudents);
        return this.selectPage(page, queryWrapper);
    }

    /**
     * 根据省份名称查询该省份所有不为空的行业名称列表（去重且按行业名排序）
     *
     * @param province 省份名称，如"云南"
     * @return 返回该省份的行业名称字符串列表
     */
    @Select("SELECT DISTINCT c.industry " +                // 查询行业名称，去重
            "FROM region_info r " +                       // 地区表别名 r
            "LEFT JOIN company_info c ON r.region_id = c.region_id " +  // 关联公司表，关联字段 region_id
            "WHERE r.province = #{province} AND c.industry IS NOT NULL " +  // 过滤指定省份且行业非空
            "ORDER BY c.industry")                         // 按行业名称升序排序
    List<String> getIndustriesByProvince(@Param("province") String province);



}

