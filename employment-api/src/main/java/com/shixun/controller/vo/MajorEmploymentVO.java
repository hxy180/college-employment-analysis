package com.shixun.controller.vo;

import lombok.Data;

/**
 * 存储单个专业的就业核心数据（对应前端employmentTop10、employmentBottom10）
 */
@Data
public class MajorEmploymentVO {
    /** 专业名称（如"人工智能"） */
    private String major;

    /** 就业率（百分比，如95.5） */
    private Double employmentRate;
}