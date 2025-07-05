package com.shixun.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shixun.controller.vo.educationSalariesVO;
import com.shixun.entity.SummaryMajor;
import com.shixun.entity.SummaryRegion;
import com.shixun.entity.SummarySalaryRange;

import java.util.Date;
import java.util.List;

/**
 * (SummarySalaryRange)表数据库访问层
 *
 * @author makejava
 * @since 2025-06-30 18:49:20
 */
public interface SummarySalaryRangeDao extends BaseMapper<SummarySalaryRange> {
    default SummarySalaryRange getMapperLatestStatDate() {
        return this.selectOne(
                new LambdaQueryWrapper<SummarySalaryRange>()
                        .select(SummarySalaryRange::getStatDate)
                        .orderByDesc(SummarySalaryRange::getStatDate)
                        .last("LIMIT 1")
        );
    }
    default List<SummarySalaryRange> getMapperSalaryRange(int gradCode){
        Date statDate = getMapperLatestStatDate().getStatDate();

        return this.selectList(
                new LambdaQueryWrapper<SummarySalaryRange>()
                        .eq(SummarySalaryRange::getGradCode,gradCode)
                        .eq(SummarySalaryRange::getStatDate,statDate)
                        .orderByDesc(SummarySalaryRange::getEmployeeCount)

        );
    }

}

