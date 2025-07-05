package com.shixun.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shixun.controller.vo.*;
import com.shixun.entity.SummarySalaryRange;

import java.util.List;

/**
 * (SummarySalaryRange)表服务接口
 *
 * @author makejava
 * @since 2025-06-30 18:49:20
 */
public interface SummarySalaryRangeService extends IService<SummarySalaryRange> {

    List<salaryRangesVO> getSalaryRange(int gradCode);

    List<educationSalariesVO> getEducationSalaryRange();

    List<IndustrySalaryVO> getIndustrySalaryRange(String type);

    SalarySummaryVO getSalarySummary();
}

