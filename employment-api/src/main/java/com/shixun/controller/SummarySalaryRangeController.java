package com.shixun.controller;



import com.shixun.controller.vo.SalarySummaryVO;
import com.shixun.controller.vo.IndustrySalaryVO;
import com.shixun.controller.vo.educationSalariesVO;
import com.shixun.controller.vo.salaryRangesVO;
import com.shixun.utils.BusinessException;
import com.shixun.utils.Result;
import static com.shixun.utils.ErrorCode.INVALID_PARAM;
import com.shixun.service.SummarySalaryRangeService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

/**
 * (SummarySalaryRange)表控制层
 *
 * @author makejava
 * @since 2025-06-30 18:49:20
 */
@RestController
@RequestMapping("/api/summarySalaryRange")
public class SummarySalaryRangeController {
    /**
     * 服务对象
     */
    @Resource
    private SummarySalaryRangeService summarySalaryRangeService;

    /**
     * 查询薪资区间分布
     * @return 所有数据
     */
    @GetMapping("/salaryRange")
    public Result< List<salaryRangesVO>> getSalaryRange(@RequestParam int gradCode) {
        return Result.success(summarySalaryRangeService.getSalaryRange(gradCode));
    }
/**
     * 查询不同学历薪资对比
     * @return 所有数据
     */
    @GetMapping("/educationSalaryRange")
    public Result< List<educationSalariesVO>> getEducationSalaryRange() {
        return Result.success(summarySalaryRangeService.getEducationSalaryRange());
    }
/**
     * 行业薪资排行
     * @return 所有数据
     */
    @GetMapping("/industrySalaryRange")
    public Result< List<IndustrySalaryVO>> getIndustrySalaryRange(@RequestParam String type) {
        // 检查参数合法性（若不为空，需是指定值）
        if (type != null && !"Top".equals(type) && !"Bottom".equals(type)) {
            throw new BusinessException(INVALID_PARAM,"请传递type参数");
        }
        return Result.success(summarySalaryRangeService.getIndustrySalaryRange(type));
    }

    /**
     * 薪资分布指标
     * @return
     */
    @GetMapping("/salarySummary")
    public Result<SalarySummaryVO> getSalarySummary() {
        SalarySummaryVO summary = summarySalaryRangeService.getSalarySummary();
        return Result.success(summary);
    }
}

