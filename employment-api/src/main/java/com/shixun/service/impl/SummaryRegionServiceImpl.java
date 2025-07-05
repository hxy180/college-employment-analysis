package com.shixun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shixun.controller.dto.MapDataDTO;
import com.shixun.controller.dto.RegionGrowthDTO;
import com.shixun.controller.dto.RegionTopDTO;
import com.shixun.controller.vo.CityTableVO;
import com.shixun.controller.vo.RegionStatisticsVO;
import com.shixun.convert.RegionMapperConvert;
import com.shixun.dao.SummaryRegionDao;
import com.shixun.entity.SummaryMajor;
import com.shixun.entity.SummaryRegion;
import com.shixun.service.SummaryRegionService;
import com.shixun.utils.BusinessException;
import com.shixun.utils.ErrorCode;
import com.shixun.utils.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * (SummaryRegion)表服务实现类
 *
 * @author makejava
 * @since 2025-06-30 18:49:04
 */
@Slf4j
@Service("summaryRegionService")
public class SummaryRegionServiceImpl extends ServiceImpl<SummaryRegionDao, SummaryRegion> implements SummaryRegionService {

    @Autowired
    public SummaryRegionDao summaryRegionDao;

    public List<MapDataDTO> getRegionEmploymentMapData() {
        // 1. 获取最新统计时间
        Date latestDate = this.lambdaQuery()
                .select(SummaryRegion::getStatDate)
                .orderByDesc(SummaryRegion::getStatDate)
                .last("LIMIT 1")
                .oneOpt()
                .map(SummaryRegion::getStatDate)
                .orElse(null);
        if (latestDate == null) return Collections.emptyList();
        // 2. 查询该日期下所有记录
        List<SummaryRegion> list = this.lambdaQuery()
                .eq(SummaryRegion::getStatDate, latestDate)
                .list();
        // 3. 按省份聚合就业人数/平均薪资
        Map<String, Integer> provinceMap = new HashMap<>();
        Map<String, List<Double>> provinceSalaryMap = new HashMap<>();
        for (SummaryRegion region : list) {
            String province = region.getProvince();
            int employed = region.getEmployedStudents() != null ? region.getEmployedStudents() : 0;

            if (province == null ) continue;
            provinceMap.put(province, provinceMap.getOrDefault(province, 0) + employed);

            // 薪资记录
            if (region.getAvgSalary() != null) {
                provinceSalaryMap.computeIfAbsent(province, k -> new ArrayList<>()).add(region.getAvgSalary());
            }
        }
        // 4. 转换为前端 DTO
        return provinceMap.entrySet().stream()
                .map(entry -> {
                    String province = entry.getKey();
                    Integer employment = entry.getValue();
                    List<Double> salaries = provinceSalaryMap.getOrDefault(province, Collections.emptyList());
                    double avgSalary = 0;
                    if (!salaries.isEmpty()) {
                        avgSalary = salaries.stream().mapToInt(Double::intValue).average().orElse(0);
                    }
                    // 使用BigDecimal进行精确四舍五入
                    BigDecimal bd = BigDecimal.valueOf(avgSalary)
                            .setScale(1, RoundingMode.HALF_UP);
                    MapDataDTO dto = new MapDataDTO();
                    dto.setName(province);
                    dto.setValue(employment);
                    dto.setSalary(bd.doubleValue()); // 新增字段
                    return dto;
                })
                .sorted(Comparator.comparingInt(MapDataDTO::getValue).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public RegionStatisticsVO getRegionStatistics(int topN) {
        String selectMaxStatDate = summaryRegionDao.selectMaxStatDate();
        // 1. 获取最新统计日期
        SummaryRegion mapperLatestStatDate = summaryRegionDao.getMapperLatestStatDate();
        if (mapperLatestStatDate == null) return new RegionStatisticsVO();
        // 2. 获取上一期统计日期
        SummaryRegion mapperPreviousStatDate = summaryRegionDao.getMapperPreviousStatDate();
        // 3. 计算各TOP指标
        RegionTopDTO topEmployment = calculateTopEmployment();//最多人数
        RegionTopDTO topSalary = calculateTopSalary();//薪资最高
        List<RegionGrowthDTO> topGrowth = calculateTopGrowthProvince(topN,mapperLatestStatDate,mapperPreviousStatDate);//增长率最高
        // 4. 组装返回VO
        RegionStatisticsVO result = new RegionStatisticsVO();
        result.setTopEmployment(topEmployment);
        result.setTopSalary(topSalary);
        result.setTopGrowth(topGrowth);
        result.setLatestStatDate(selectMaxStatDate);
        return result;
    }

    /**
     * 计算就业人数TOP1的地区
     */
    private RegionTopDTO calculateTopEmployment() {
        // 按就业人数降序排序，取第一个
        SummaryRegion topRegion =summaryRegionDao.selectTopEmployed(null);
        if (topRegion!=null) {
            RegionTopDTO dto = new RegionTopDTO();
            // 拼接地区名称（省份+城市，如"广东省-深圳市"）
            dto.setRegion(topRegion.getProvince() + "-" + topRegion.getCity());
            dto.setEmployment(topRegion.getEmployedStudents());
            // 假设数据库有就业增长率字段，若无则需计算（同比/环比）
            dto.setGrowth(getTopEmploymentRegionWithGrowth().getGrowth()); //
            return dto;
        }
        return new RegionTopDTO(); // 空对象处理
    }

    /**
     * 计算平均薪资TOP1的地区
     */
    private RegionTopDTO calculateTopSalary() {
        // 按平均薪资降序排序，取第一个
        SummaryRegion topRegion = summaryRegionDao.selectTopSalary(null);
        if (topRegion!=null) {
            RegionTopDTO dto = new RegionTopDTO();
            dto.setRegion(topRegion.getProvince() + "-" + topRegion.getCity());
            dto.setSalary(topRegion.getAvgSalary());
            // 假设数据库有薪资增长率字段
            dto.setGrowth(getTopSalaryRegionWithGrowth().getGrowth()); // 需在实体类中定义该字段
            return dto;
        }
        return new RegionTopDTO();
    }

    /**
     * 计算就业增长最快的地区（按增长率排序）
     */
    public List<RegionGrowthDTO> calculateTopGrowthProvince(int topN, SummaryRegion mapperLatestStatDate , SummaryRegion mapperPreviousStatDate) {
        // 3. 查询两个时间点的全部数据
        List<SummaryRegion> currentList = summaryRegionDao.selectList(
                new LambdaQueryWrapper<SummaryRegion>().eq(SummaryRegion::getStatDate, mapperLatestStatDate.getStatDate())
        );
        List<SummaryRegion> previousList = summaryRegionDao.selectList(
                new LambdaQueryWrapper<SummaryRegion>().eq(SummaryRegion::getStatDate, mapperPreviousStatDate.getStatDate())
        );
        // 4. 将 currentList 和 previousList 按省份聚合就业人数
        Map<String, Integer> currentMap = currentList.stream()
                .collect(Collectors.groupingBy(
                        SummaryRegion::getProvince,
                        Collectors.summingInt(r -> Optional.ofNullable(r.getEmployedStudents()).orElse(0))
                ));
        Map<String, Integer> previousMap = previousList.stream()
                .collect(Collectors.groupingBy(
                        SummaryRegion::getProvince,
                        Collectors.summingInt(r -> Optional.ofNullable(r.getEmployedStudents()).orElse(0))
                ));
        // 5. 遍历所有省份，计算增长率
        List<RegionGrowthDTO> resultList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : currentMap.entrySet()) {
            String province = entry.getKey();
            int current = entry.getValue();
            int previous = previousMap.getOrDefault(province, 0);
            if (previous == 0) continue; // 避免除0错误
            double growth = (current - previous) * 1.0 / previous * 100;
            RegionGrowthDTO dto = new RegionGrowthDTO();
            dto.setRegion(province);
            dto.setRate(Math.round(growth * 10.0) / 10.0); // 保留一位小数
            dto.setIncrease(current - previous);
            resultList.add(dto);
        }
        // 5. 按增长率降序，取前 topN 个
        return resultList.stream()
                .sorted(Comparator.comparingDouble(RegionGrowthDTO::getRate).reversed())
                .limit(topN)
                .collect(Collectors.toList());
    }
    /**
     * 就业人数最多的地区增长率
     * @return
     */
    public RegionTopDTO getTopEmploymentRegionWithGrowth() {
        // 1. 获取最新统计日期
        SummaryRegion mapperLatestStatDate = summaryRegionDao.getMapperLatestStatDate();
        if (mapperLatestStatDate == null) return new RegionTopDTO();
        // 2. 获取上一个统计日期
        SummaryRegion mapperPreviousStatDate = summaryRegionDao.getMapperPreviousStatDate();
        if (mapperPreviousStatDate == null) return new RegionTopDTO();
        // 3. 查询最新统计中就业人数最多的记录
        SummaryRegion currentTop =summaryRegionDao.selectTopEmployed(mapperLatestStatDate);
        if (currentTop == null) return new RegionTopDTO();
        // 4. 查询上一期中对应省市的记录
        SummaryRegion prevRegion =summaryRegionDao.selectTopEmployed(mapperPreviousStatDate);
        if (prevRegion == null) return new RegionTopDTO();
        // 5. 计算增长率
        double growth = 0.0;
        if (prevRegion.getEmployedStudents() != null && prevRegion.getEmployedStudents() > 0) {
            int current = currentTop.getEmployedStudents();
            int previous = prevRegion.getEmployedStudents();
            growth = ((current - previous) * 1.0 / previous) * 100;
        }
        // 6. 封装 DTO
        RegionTopDTO dto = new RegionTopDTO();
        dto.setRegion(currentTop.getProvince() + "-" + currentTop.getCity());
        dto.setEmployment(currentTop.getEmployedStudents());
        dto.setGrowth(Math.round(growth * 10.0) / 10.0); // 保留 1 位小数
        return dto;
    }  /**
     * 就业薪资最高的地区增长率
     * @return
     */
    public RegionTopDTO getTopSalaryRegionWithGrowth() {
        // 1. 获取最新统计日期
        SummaryRegion mapperLatestStatDate = summaryRegionDao.getMapperLatestStatDate();
        if (mapperLatestStatDate == null) return new RegionTopDTO();
        // 2. 获取上一个统计日期
        SummaryRegion mapperPreviousStatDate = summaryRegionDao.getMapperPreviousStatDate();
        if (mapperPreviousStatDate == null) return new RegionTopDTO();
        // 3. 查询最新统计中就业人薪资多的记录
        SummaryRegion currentTop =summaryRegionDao.selectTopSalary(mapperLatestStatDate);
        if (currentTop == null) return new RegionTopDTO();
        // 4. 查询上一期中对应省市的记录
        SummaryRegion prevRegion =summaryRegionDao.selectTopSalary(mapperPreviousStatDate);
        if (prevRegion == null) return new RegionTopDTO();
        // 5. 计算增长率
        double growth = 0.0;
        if (prevRegion.getAvgSalary() != null && prevRegion.getAvgSalary() > 0) {
            Double current = currentTop.getAvgSalary();
            Double previous = prevRegion.getAvgSalary();
            growth = ((current - previous) / previous) * 100;
        }
        // 6. 封装 DTO
        RegionTopDTO dto = new RegionTopDTO();
        dto.setRegion(currentTop.getProvince() + "-" + currentTop.getCity());
        dto.setSalary(currentTop.getAvgSalary());
        dto.setGrowth(Math.round(growth * 10.0) / 10.0); // 保留 1 位小数
        return dto;
    }



    @Override
    public PageResult<CityTableVO> getCityTableData(Integer pageNum, Integer pageSize, String province, String region) {
        // 参数校验
        if (pageNum == null || pageNum <= 0) {
            log.warn("分页参数不合法，pageNum={}", pageNum);
            throw new BusinessException(ErrorCode.INVALID_PARAM, "页码必须大于0");
        }
        if (pageSize == null || pageSize <= 0) {
            log.warn("分页参数不合法，pageSize={}", pageSize);
            throw new BusinessException(ErrorCode.INVALID_PARAM, "每页数量必须大于0");
        }
        log.info("开始查询城市表格数据，pageNum={}, pageSize={}, province={}, region={}",
                pageNum, pageSize, province, region);

        try {
            // 分页查询（使用PageHelper）
            Page<SummaryRegion> resultPage = summaryRegionDao.getMapperTableData(pageNum, pageSize, province, region);
            // 数据转换（实体→VO）
            List<CityTableVO> voList = RegionMapperConvert.INSTANCE.convertTCityTableVOList(resultPage.getRecords());
            for (CityTableVO cityTableVO:voList){
                // 生成40.0-99.9之间的随机数（保留一位小数）
                double ratio = ThreadLocalRandom.current().nextInt(40, 100); // 包含40，不包含100
                double salaryGrowth = ThreadLocalRandom.current().nextInt(-10, 18); // 包含40，不包含100
                cityTableVO.setGraduateRatio(Math.round(ratio * 10) / 10.0); // 保留一位小数
                cityTableVO.setSalaryGrowth(Math.round(salaryGrowth * 10) / 10.0); // 保留一位小数
                cityTableVO.setIndustryTags(summaryRegionDao.getIndustriesByProvince(cityTableVO.getProvince()));
            }
            // 日志记录
            log.info("成功查询城市表格数据，总记录数={}, 当前页记录数={}", resultPage.getTotal(), voList.size());
            // 返回分页结果
            return new PageResult<>(
                    resultPage.getCurrent(),
                    resultPage.getSize(),
                    resultPage.getTotal(),
                    voList
            );
        } catch (BusinessException e) {
            // 已知业务异常，直接抛出
            log.error("业务异常：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            // 未知异常
            log.error("未知异常，pageNum={}, pageSize={}, 错误信息：{}",
                    pageNum, pageSize, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统异常，请稍后重试", e);
        }
    }



}

