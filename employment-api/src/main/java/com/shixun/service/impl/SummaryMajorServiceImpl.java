package com.shixun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shixun.controller.dto.IndustryDistributionDTO;
import com.shixun.controller.vo.MajorDetailVO;
import com.shixun.controller.vo.MajorEmploymentVO;
import com.shixun.controller.vo.MajorSalaryVO;
import com.shixun.controller.vo.SankeyDataVO;
import com.shixun.convert.MajorMapperConvert;
import com.shixun.dao.SummaryMajorDao;
import com.shixun.entity.SummaryMajor;
import com.shixun.service.SummaryMajorService;
import com.shixun.utils.BusinessException;
import com.shixun.utils.ErrorCode;
import com.shixun.utils.GlobalExceptionHandler;
import com.shixun.utils.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static com.shixun.utils.ErrorCode.INVALID_PARAM;

/**
 * (SummaryMajor)表服务实现类
 *
 * @author makejava
 * @since 2025-06-30 18:48:36
 */
@Service("summaryMajorService")
@Slf4j
public class SummaryMajorServiceImpl extends ServiceImpl<SummaryMajorDao, SummaryMajor> implements SummaryMajorService {

    @Autowired
    SummaryMajorDao summaryMajorDao;
    public List<IndustryDistributionDTO> getIndustryDistribution() {
        // 1. 查询最新统计日期
        Date latestDate = this.lambdaQuery()
                .select(SummaryMajor::getStatDate)
                .orderByDesc(SummaryMajor::getStatDate)
                .last("LIMIT 1")
                .oneOpt()
                .map(SummaryMajor::getStatDate)
                .orElse(null);

        if (latestDate == null) return Collections.emptyList();

        // 2. 查询该日期下所有记录
        List<SummaryMajor> list = this.lambdaQuery()
                .eq(SummaryMajor::getStatDate, latestDate)
                .list();

        if (list.isEmpty()) return Collections.emptyList();

        // 3. 汇总每个行业的就业人数
        Map<String, Integer> industryMap = new HashMap<>();
        int totalEmployed = 0;

        for (SummaryMajor item : list) {
            String industry = item.getTopIndustry();
            int employed = item.getEmployedStudents() != null ? item.getEmployedStudents() : 0;

            if (industry == null|| industry.isEmpty()) industry = "其他";

            industryMap.put(industry, industryMap.getOrDefault(industry, 0) + employed);
            totalEmployed += employed;
        }

        // 4. 转换为 DTO 列表
        int finalTotalEmployed = totalEmployed;
        return industryMap.entrySet().stream()
                .map(entry -> {
                    IndustryDistributionDTO dto = new IndustryDistributionDTO();
                    dto.setName(entry.getKey());
                    double rate = finalTotalEmployed == 0 ? 0.0 : entry.getValue() * 100.0 / finalTotalEmployed;
                    dto.setValue(Math.round(rate * 10.0) / 10.0); // 保留1位小数
                    log.info("dto"+dto);
                    return dto;
                })
                .sorted(Comparator.comparingDouble(IndustryDistributionDTO::getValue).reversed())
                .collect(Collectors.toList());
    }


    @Override
    public List<MajorEmploymentVO> getTop10ByEmploymentRate() {
        log.info("开始查询就业率TOP10的专业数据");
        try {
            // 修正变量名，与方法逻辑保持一致
            List<SummaryMajor> top10Majors = summaryMajorDao.getMapperTop10ByEmploymentRate();

            // 空值检查
            if (CollectionUtils.isEmpty(top10Majors)) {
                log.warn("未查询到就业率TOP10的专业数据");
                return Collections.emptyList();
            }

            log.info("成功查询到{}条就业率TOP10的专业数据", top10Majors.size());
            return MajorMapperConvert.INSTANCE.toEmploymentVOList(top10Majors);
        } catch (Exception e) {
            log.error("查询就业率TOP10的专业数据失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.DATA_QUERY_FAILED, "查询就业率TOP10数据失败", e);
        }
    }

    @Override
    public List<MajorEmploymentVO> getBottom10ByEmploymentRate() {
        log.info("开始查询就业率BOTTOM10的专业数据");
        try {
            // 修正变量名，与方法逻辑保持一致
            List<SummaryMajor> bottom10Majors = summaryMajorDao.getMapperBottom10ByEmploymentRate();
            // 空值检查
            if (CollectionUtils.isEmpty(bottom10Majors)) {
                log.warn("未查询到就业率BOTTOM10的专业数据");
                return Collections.emptyList();
            }
            log.info("成功查询到{}条就业率BOTTOM10的专业数据", bottom10Majors.size());
            return MajorMapperConvert.INSTANCE.toEmploymentVOList(bottom10Majors);
        } catch (Exception e) {
            log.error("查询就业率BOTTOM10的专业数据失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.DATA_QUERY_FAILED, "查询就业率BOTTOM10数据失败", e);
        }
    }

    @Override
    public List<MajorSalaryVO> getSalaryData(Integer limit) throws BusinessException {
        // 参数校验
        if (limit == null || limit <= 0) {
            log.warn("分页参数不合法，limit={}", limit);
            throw new BusinessException(INVALID_PARAM);
        }
        log.info("开始获取薪资数据，limit={}", limit);
        try {
            List<SummaryMajor> salaryDataList = summaryMajorDao.getMapperSalaryData(limit);
            // 空值处理
            if (CollectionUtils.isEmpty(salaryDataList)) {
                log.warn("根据条件查询薪资数据为空，limit={}", limit);
                return Collections.emptyList();
            }
            log.debug("成功获取{}条薪资数据", salaryDataList.size());
            return MajorMapperConvert.INSTANCE.toSalaryVOList(salaryDataList);
        } catch (Exception e) {
            log.error("未知异常: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.UNKNOWN_ERROR, "系统异常，请稍后重试", e);
        }
    }

//    @Override/**/
//    public SankeyDataVO getSankeyData() {
//        List<SummaryMajor> list = this.getBaseMapper().selectList(
//                new LambdaQueryWrapper<SummaryMajor>()
//                        .eq(SummaryMajor::getStatDate, getLatestStatDate())
//        );
//        return buildSankeyData(list);
//    }

    public PageResult<MajorDetailVO> getTableData(Integer pageNum, Integer pageSize, String majorName, String topIndustry) {
        if (pageNum == null || pageNum <= 0) {
            log.warn("分页参数不合法，pageNum={}", pageNum);
            throw new BusinessException(ErrorCode.INVALID_PARAM, "页码必须大于0");
        }
        if (pageSize == null || pageSize <= 0) {
            log.warn("分页参数不合法，pageSize={}", pageSize);
            throw new BusinessException(ErrorCode.INVALID_PARAM, "每页数量必须大于0");
        }
        log.info("开始查询专业表格数据，pageNum={}, pageSize={}, majorName={}, topIndustry={}",
                pageNum, pageSize, majorName, topIndustry);

        try {
            // 1️⃣ 分页查询基础数据
            Page<SummaryMajor> resultPage = summaryMajorDao.getMapperTableData(pageNum, pageSize, majorName, topIndustry);
            List<SummaryMajor> records = resultPage.getRecords();

            // 2️⃣ 查询所有专业就业总人数（用于计算总占比）
            Integer totalEmployment = summaryMajorDao.getTotalEmploymentByYear(summaryMajorDao.getMapperLatestStatDate().getStatDate());
            if (totalEmployment == null || totalEmployment == 0) {
                totalEmployment = 1; // 防止除0
            }
            log.warn("总就业人数：{}", totalEmployment);

            // 3️⃣ 转换并计算占比
            Integer finalTotalEmployment = totalEmployment;
            List<MajorDetailVO> voList = records.stream().map(record -> {
                MajorDetailVO vo = MajorMapperConvert.INSTANCE.toDetailVO(record);

                Integer employed = record.getEmployedStudents(); // 专业就业人数
                vo.setEmploymentCount(employed); // 设置专用字段

                double percent = (employed *100.0)/ (finalTotalEmployment);
                vo.setIndustryPercent(Math.round(percent * 100) / 100.00); // 保留1位小数

                return vo;
            }).collect(Collectors.toList());

            // 4️⃣ 返回分页结果
            return new PageResult<>(
                    resultPage.getCurrent(),
                    resultPage.getSize(),
                    resultPage.getTotal(),
                    voList
            );
        } catch (BusinessException e) {
            log.error("业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("未知异常，pageNum={}, pageSize={}, 错误信息：{}", pageNum, pageSize, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统异常，请稍后重试", e);
        }
    }



}

