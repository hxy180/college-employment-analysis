package com.shixun.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shixun.dao.SalaryInfoDao;
import com.shixun.entity.SalaryInfo;
import com.shixun.service.SalaryInfoService;
import org.springframework.stereotype.Service;

/**
 * (SalaryInfo)表服务实现类
 *
 * @author makejava
 * @since 2025-06-30 18:46:42
 */
@Service("salaryInfoService")
public class SalaryInfoServiceImpl extends ServiceImpl<SalaryInfoDao, SalaryInfo> implements SalaryInfoService {

}

