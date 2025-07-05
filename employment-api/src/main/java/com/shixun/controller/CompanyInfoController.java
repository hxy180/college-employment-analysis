package com.shixun.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shixun.utils.Result;

import static com.shixun.utils.Result.success;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shixun.entity.CompanyInfo;
import com.shixun.service.CompanyInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * (CompanyInfo)表控制层
 *
 * @author makejava
 * @since 2025-06-30 18:34:21
 */
@RestController
@RequestMapping("companyInfo")
public class CompanyInfoController {
    /**
     * 服务对象
     */
    @Resource
    private CompanyInfoService companyInfoService;

    /**
     * 分页查询所有数据
     *
     * @param page        分页对象
     * @param companyInfo 查询实体
     * @return 所有数据
     */
    @GetMapping
    public Result selectAll(Page<CompanyInfo> page, CompanyInfo companyInfo) {
        return success(this.companyInfoService.page(page, new QueryWrapper<>(companyInfo)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public Result selectOne(@PathVariable Serializable id) {
        return success(this.companyInfoService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param companyInfo 实体对象
     * @return 新增结果
     */
    @PostMapping
    public Result insert(@RequestBody CompanyInfo companyInfo) {
        return success(this.companyInfoService.save(companyInfo));
    }

    /**
     * 修改数据
     *
     * @param companyInfo 实体对象
     * @return 修改结果
     */
    @PutMapping
    public Result update(@RequestBody CompanyInfo companyInfo) {
        return success(this.companyInfoService.updateById(companyInfo));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public Result delete(@RequestParam("idList") List<Long> idList) {
        return success(this.companyInfoService.removeByIds(idList));
    }
}

