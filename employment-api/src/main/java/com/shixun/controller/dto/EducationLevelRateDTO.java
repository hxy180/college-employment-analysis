package com.shixun.controller.dto;

import lombok.Data;

@Data
public class EducationLevelRateDTO {
    /**
     * 学历层次
     * 取值范围：专科、本科、硕士、博士等
     */
    private String level;

    /**
     * 该学历层次在自己总体中的占比（百分比）
     * 取值范围：0.0-100.0
     */
    private Double rate;
}