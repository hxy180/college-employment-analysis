package com.shixun.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shixun.controller.dto.IndustryDistributionDTO;
import com.shixun.controller.vo.MajorDetailVO;
import com.shixun.controller.vo.MajorEmploymentVO;
import com.shixun.controller.vo.MajorSalaryVO;
import com.shixun.entity.SummaryMajor;
import com.shixun.utils.BusinessException;
import com.shixun.utils.PageResult;

import java.util.List;

/**
 * 专业就业统计服务接口
 * 负责处理与 SummaryMajor 表相关的业务逻辑，如行业分布、就业率、薪资、表格分页等功能。
 *
 * @author makejava
 * @since 2025-06-30 18:48:36
 */
public interface SummaryMajorService extends IService<SummaryMajor> {

    /**
     * 获取各专业对应的行业分布（用于行业分布图）
     *
     * @return 行业分布数据列表
     */
    List<IndustryDistributionDTO> getIndustryDistribution();

    // ==================== 就业率相关 ====================

    /**
     * 获取就业率最高的前10个专业
     *
     * @return 高就业率专业列表
     */
    List<MajorEmploymentVO> getTop10ByEmploymentRate();

    /**
     * 获取就业率最低的后10个专业
     *
     * @return 低就业率专业列表
     */
    List<MajorEmploymentVO> getBottom10ByEmploymentRate();

    // ==================== 薪资相关 ====================

    /**
     * 获取专业对应的平均薪资信息
     *
     * @param limit 限制返回条数（如前10名）
     * @return 薪资数据列表
     */
    List<MajorSalaryVO> getSalaryData(Integer limit) throws BusinessException;

//    // ==================== 桑基图相关 ====================
//
//    /**
//     * 获取桑基图所需的数据（专业-行业-城市流向）
//     *
//     * @return 桑基图数据
//     */
//    SankeyDataVO getSankeyData();

    // ==================== 表格数据分页筛选 ====================

    /**
     * 分页获取专业详情表格数据（支持条件筛选）
     *
     * @param pageNum     页码
     * @param pageSize    每页大小
     * @param majorName   专业名称关键词（模糊搜索）
     * @param topIndustry 所属行业（可选条件）
     * @return 分页结果
     */
    PageResult<MajorDetailVO> getTableData(Integer pageNum, Integer pageSize, String majorName, String topIndustry);
}
