package com.shixun.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shixun.controller.dto.EducationLevelRateDTO;
import com.shixun.controller.dto.OverviewStatsDTO;
import com.shixun.controller.vo.OverviewVO;
import com.shixun.dao.SummaryOverviewDao;
import com.shixun.entity.SummaryOverview;
import com.shixun.service.SummaryMajorService;
import com.shixun.service.SummaryOverviewService;
import com.shixun.service.SummaryRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * (SummaryOverview)表服务实现类
 *
 * @author makejava
 * @since 2025-06-30 18:48:50
 */
@Service("summaryOverviewService")
public class SummaryOverviewServiceImpl extends ServiceImpl<SummaryOverviewDao, SummaryOverview> implements SummaryOverviewService {

    @Autowired
    public SummaryOverviewDao summaryOverviewDao;

    @Autowired
    public SummaryMajorService summaryMajorService;

    @Autowired
    public SummaryRegionService summaryRegionService;

    /**
     * 获取最新的学历维度统计概览
     *
     * @return 最新统计日期的各学历层次概览数据列表
     */
    public List<SummaryOverview> getLatestEducationSummary() {
        return summaryOverviewDao.selectLatestSummary();
    }


    /**
     * 查询最新统计日期的全量学历维度概览数据
     *
     * @return 最新统计日期的所有学历层次概览数据
     * 如果没有数据则返回空
     */
    public OverviewVO getLatestOverview() {
        OverviewVO overviewVO =new OverviewVO();
        overviewVO.setOverviewStatsVO(getOverviewStats());
        overviewVO.setEducationLevelRateVO(getEducationLevelRateDTO());
        overviewVO.setIndustryDistributionVO(summaryMajorService.getIndustryDistribution());
        overviewVO.setMapDataVO(summaryRegionService.getRegionEmploymentMapData());
        return overviewVO;
    }


    public List<EducationLevelRateDTO> getEducationLevelRateDTO() {
        // 获取最新统计时间
        Date latestDate = this.lambdaQuery()
                .select(SummaryOverview::getStatDate)
                .orderByDesc(SummaryOverview::getStatDate)
                .last("LIMIT 1")
                .oneOpt()
                .map(SummaryOverview::getStatDate)
                .orElse(null);
        if (latestDate == null) {
            return null;
        }
        // 获取该日期下所有学历数据
        List<SummaryOverview> list = this.lambdaQuery()
                .eq(SummaryOverview::getStatDate, latestDate)
                .list();
        int total = list.stream().mapToInt(SummaryOverview::getTotalStudents).sum();
        if (total == 0) {
            return null;
        }
        // 转换为占比结构
        return list.stream()
                .sorted(Comparator.comparingDouble(SummaryOverview::getEmploymentRate).reversed()) // 降序
                .map(item -> {
                    EducationLevelRateDTO dto = new EducationLevelRateDTO();
                    dto.setLevel(item.getEducationLevel());
                    dto.setRate(BigDecimal.valueOf(item.getEmploymentRate() * 100.0)
                            .setScale(1, RoundingMode.HALF_UP)
                            .doubleValue());
                    return dto;
                })
                .collect(Collectors.toList());

    }

    public OverviewStatsDTO getOverviewStats(){
        OverviewStatsDTO overviewStatsDTO =new OverviewStatsDTO();
        // 查询数据库中最新的统计日期
        SummaryOverview latestRecord = this.lambdaQuery()
                .orderByDesc(SummaryOverview::getStatDate) // 按统计日期降序排列
                .last("LIMIT 1") // 取第一条记录（即最新日期）
                .one();
        // 处理可能的空结果情况
        if (latestRecord == null || latestRecord.getStatDate() == null) {
            log.warn("getOverviewStats未查询到统计数据记录");
            return null;
        }
        Date latestDate = latestRecord.getStatDate();
        // 查询该日期下的所有学历维度数据
       List<SummaryOverview> list=this.lambdaQuery()
                .eq(SummaryOverview::getStatDate, latestDate) // 筛选特定日期
                .list();

// 求和与平均
        int totalGraduates = list.stream().mapToInt(SummaryOverview::getTotalStudents).sum();
        int employed = list.stream().mapToInt(SummaryOverview::getEmployedStudents).sum();
        double avgSalary = list.stream().mapToDouble(SummaryOverview::getAvgSalary).average().orElse(0.0);
        double employmentRate = totalGraduates == 0 ? 0.0 : (employed * 1.0 / totalGraduates * 100);

// 封装 VO
        overviewStatsDTO.setTotalGraduates(totalGraduates);
        overviewStatsDTO.setEmployed(employed);
        overviewStatsDTO.setAvgSalary(Math.round(avgSalary * 100.0) / 100.0);
        overviewStatsDTO.setEmploymentRate(Math.round(employmentRate * 10.00) / 10.00);

       return overviewStatsDTO;
    }

}

