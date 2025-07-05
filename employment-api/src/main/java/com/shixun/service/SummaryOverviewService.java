package com.shixun.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shixun.controller.vo.OverviewVO;
import com.shixun.entity.SummaryOverview;

import java.util.List;
import java.util.Map;

/**
 * (SummaryOverview)表服务接口
 *
 * @author makejava
 * @since 2025-06-30 18:48:50
 */
public interface SummaryOverviewService extends IService<SummaryOverview> {


    /**
     * 生成学历维度统计报告（供管理后台使用）
     *
     * @return 结构化统计报告，包含：
     *         - timestamp: 报告生成时间
     *         - data: 最新统计数据列表（按学历排序）
     *         - totalStudents: 总学生人数
     *         - totalEmployed: 总就业人数
     */
    public OverviewVO getLatestOverview();



}

