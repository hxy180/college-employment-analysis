package com.shixun.controller;



import com.shixun.controller.vo.MajorDetailVO;
import com.shixun.controller.vo.MajorEmploymentVO;
import com.shixun.controller.vo.MajorSalaryVO;
import com.shixun.utils.PageResult;
import com.shixun.utils.Result;
import com.shixun.service.SummaryMajorService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * (SummaryMajor)表控制层
 *
 * @author makejava
 * @since 2025-06-30 18:48:35
 */
@RestController
@RequestMapping("/api/summaryMajor")
public class SummaryMajorController {
    /**
     * 服务对象
     */
    @Resource
    private SummaryMajorService summaryMajorService;
    /**
     * 获取就业率TOP10的专业
     */
    @GetMapping("/employment/top10")
    public Result<List<MajorEmploymentVO>> getTop10ByEmploymentRate() {
        List<MajorEmploymentVO> data = summaryMajorService.getTop10ByEmploymentRate();
        return Result.success(data);
    }

    /**
     * 获取就业率BOTTOM10的专业
     */
    @GetMapping("/employment/bottom10")
    public Result<List<MajorEmploymentVO>> getBottom10ByEmploymentRate() {
        List<MajorEmploymentVO> data = summaryMajorService.getBottom10ByEmploymentRate();
        return Result.success(data);
    }

    /**
     * 获取薪资数据（按薪资排序）
     */
    @GetMapping("/salary/list")
    public Result<List<MajorSalaryVO>> getSalaryData() {
        List<MajorSalaryVO> data = summaryMajorService.getSalaryData(10);
        return Result.success(data);
    }

    /**
     * 获取专业详情表格数据（支持分页、筛选）
     */
    @GetMapping("/table/list")
    public Result<PageResult<MajorDetailVO>> getTableData(
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String majorName, // 按专业名称筛选
            @RequestParam(required = false) String topIndustry // 按行业筛选
    ) {
        PageResult<MajorDetailVO> data = summaryMajorService.getTableData(
                pageNum, pageSize, majorName, topIndustry);
        return Result.success(data);
    }



}

