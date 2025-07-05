package com.shixun.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shixun.controller.vo.*;
import com.shixun.dao.SummaryMajorDao;
import com.shixun.dao.SummaryOverviewDao;
import com.shixun.dao.SummarySalaryRangeDao;
import com.shixun.entity.SummaryMajor;
import com.shixun.entity.SummaryOverview;
import com.shixun.entity.SummarySalaryRange;
import com.shixun.service.SummarySalaryRangeService;
import com.shixun.utils.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

import static com.shixun.utils.ErrorCode.DATA_EMPTY;

/**
 * (SummarySalaryRange)表服务实现类
 *
 * @author makejava
 * @since 2025-06-30 18:49:20
 */
@Service("summarySalaryRangeService")
public class SummarySalaryRangeServiceImpl extends ServiceImpl<SummarySalaryRangeDao, SummarySalaryRange> implements SummarySalaryRangeService {

    @Autowired
    public SummarySalaryRangeDao summarySalaryRangeDao;

    @Autowired
    public SummaryOverviewDao summaryOverviewDao;
    @Autowired
    public SummaryMajorDao summaryMajorDao;

    @Override
    public  List<salaryRangesVO> getSalaryRange(int gradCode) {
        // 1. 查询原始薪资区间数据
        List<SummarySalaryRange> salaryRanges = summarySalaryRangeDao.getMapperSalaryRange(gradCode);
        if (salaryRanges == null || salaryRanges.isEmpty()) {
            // 处理空数据（返回空结果或抛出异常，根据业务需求调整）
          throw new BusinessException(DATA_EMPTY);
        }
        // 2. 计算总人数（用于计算占比）
        int totalCount = salaryRanges.stream()
                .mapToInt(SummarySalaryRange::getEmployeeCount)
                .sum();
        // 3. 转换为 VO 列表（包含薪资区间、占比、随机颜色）
        List<salaryRangesVO> voList = new ArrayList<>();
        for (SummarySalaryRange range : salaryRanges) {
            salaryRangesVO vo = new salaryRangesVO();
            // 设置薪资区间（如 "3k-5k"）
            vo.setSalary_range(range.getSalaryRange());
            // 计算占比（保留两位小数，转换为百分比字符串）
            double rate = totalCount > 0 ? (double) range.getEmployeeCount() / totalCount * 100 : 0;
            vo.setRate(String.format("%.2f%%", rate));
            // 生成随机颜色（RGB格式，如 "#FF5733"）
            vo.setColor(String.format("#%06X", new Random().nextInt(0xFFFFFF + 1)));
            voList.add(vo);
        }
        // 4. 封装到返回结果对象
        return voList;
    }

    @Override
    public List<educationSalariesVO> getEducationSalaryRange() {
        List<SummaryOverview> salaryRanges = summaryOverviewDao.getEducationSalaryRange();
        if(salaryRanges.isEmpty()){
            throw new BusinessException(DATA_EMPTY);
        }
        // 2. 转换为VO列表（仅保留前端所需的学历和平均薪资字段）
        List<educationSalariesVO> result = new ArrayList<>();
        for (SummaryOverview overview : salaryRanges) {
            educationSalariesVO vo = new educationSalariesVO();
            vo.setEducation(overview.getEducationLevel()); // 映射学历字段（如"本科"）
            vo.setAvgSalary(overview.getAvgSalary());     // 映射平均薪资字段
            result.add(vo);
        }
        return result;
    }

    @Override
    public List<IndustrySalaryVO> getIndustrySalaryRange(String type) {
        List<SummaryMajor> IndustrySalaryList;
        if ("Top".equals(type)){
          IndustrySalaryList = summaryMajorDao.getMapperTop10ByIndustrySalary();
        } else if ("Bottom".equals(type)) {
            IndustrySalaryList = summaryMajorDao.getMapperBottom10ByIndustrySalary();
        } else {
            IndustrySalaryList = summaryMajorDao.getAllByIndustrySalary();
        }
        if (IndustrySalaryList.isEmpty()){
            throw new BusinessException(DATA_EMPTY);
        }
        // 2. 转换为行业薪资VO列表
        List<IndustrySalaryVO> result = new ArrayList<>();
        for (SummaryMajor major : IndustrySalaryList) {
            IndustrySalaryVO vo = new IndustrySalaryVO();
            vo.setMajorName(major.getMajorName());
            vo.setIndustry(major.getTopIndustry()); // 映射主要就业行业
            vo.setAvgSalary(major.getAvgSalary());  // 映射该行业对应的平均薪资
            result.add(vo);
        }
        return result;

    }

    @Override
    public SalarySummaryVO getSalarySummary() {
        SalarySummaryVO salarySummaryVO = new SalarySummaryVO();
        salarySummaryVO.setAvgSalary(8450.0);
        salarySummaryVO.setMedianSalary(7800.0);
        salarySummaryVO.setSalaryGrowth(6.7);
        salarySummaryVO.setTopIndustry("信息技术");
        salarySummaryVO.setTopIndustrySalary(15200.0);
        salarySummaryVO.setHighSalaryPercent(7.9);
        return salarySummaryVO;
    }
}

