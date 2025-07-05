package com.shixun.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shixun.controller.vo.EducationRateVO;
import com.shixun.entity.StudentInfo;

import java.util.List;

/**
 * (StudentInfo)表服务接口
 *
 * @author makejava
 * @since 2025-06-30 18:47:06
 */
public interface StudentInfoService extends IService<StudentInfo> {
    /**
     * 学历层次人数占比
     * @return
     */
    List<EducationRateVO> getEducationRate();
}

