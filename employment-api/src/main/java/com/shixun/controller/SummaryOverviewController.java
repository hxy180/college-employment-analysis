package com.shixun.controller;

import com.shixun.controller.vo.OverviewVO;
import com.shixun.service.SummaryOverviewService;
import com.shixun.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@RequestMapping("/api/overview")
public class SummaryOverviewController {

    @Autowired
    private SummaryOverviewService summaryOverviewService;


    @GetMapping("/summaryStats")
    public Result<?> getOverviewSummaryData() {
        OverviewVO latestRecords = summaryOverviewService.getLatestOverview();
        log.info("查询结果{}",latestRecords);
        return Result.success(latestRecords);

    }

}