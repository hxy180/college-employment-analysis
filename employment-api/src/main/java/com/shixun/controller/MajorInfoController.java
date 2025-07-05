package com.shixun.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shixun.utils.Result;

import static com.shixun.utils.Result.success;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shixun.entity.MajorInfo;
import com.shixun.service.MajorInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * (MajorInfo)表控制层
 *
 * @author makejava
 * @since 2025-06-30 18:45:47
 */
@RestController
@RequestMapping("majorInfo")
public class MajorInfoController {
    /**
     * 服务对象
     */
    @Resource
    private MajorInfoService majorInfoService;

    /**
     * 分页查询所有数据
     *
     * @param page      分页对象
     * @param majorInfo 查询实体
     * @return 所有数据
     */
    @GetMapping
    public Result selectAll(Page<MajorInfo> page, MajorInfo majorInfo) {
        return success(this.majorInfoService.page(page, new QueryWrapper<>(majorInfo)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public Result selectOne(@PathVariable Serializable id) {
        return success(this.majorInfoService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param majorInfo 实体对象
     * @return 新增结果
     */
    @PostMapping
    public Result insert(@RequestBody MajorInfo majorInfo) {
        return success(this.majorInfoService.save(majorInfo));
    }

    /**
     * 修改数据
     *
     * @param majorInfo 实体对象
     * @return 修改结果
     */
    @PutMapping
    public Result update(@RequestBody MajorInfo majorInfo) {
        return success(this.majorInfoService.updateById(majorInfo));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public Result delete(@RequestParam("idList") List<Long> idList) {
        return success(this.majorInfoService.removeByIds(idList));
    }
}

