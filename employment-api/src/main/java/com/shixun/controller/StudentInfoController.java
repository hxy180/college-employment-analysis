package com.shixun.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shixun.utils.Result;

import static com.shixun.utils.Result.success;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shixun.entity.StudentInfo;
import com.shixun.service.StudentInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * (StudentInfo)表控制层
 *
 * @author makejava
 * @since 2025-06-30 18:47:06
 */
@RestController
@RequestMapping("/api/studentInfo")
public class StudentInfoController {
    /**
     * 服务对象
     */
    @Resource
    private StudentInfoService studentInfoService;

    /**
     * 分页查询所有数据
     * @return 所有数据
     */
    @GetMapping("/educationRate")
    public Result<?> getEducationRate() {
        return success(studentInfoService.getEducationRate());
    }


}

