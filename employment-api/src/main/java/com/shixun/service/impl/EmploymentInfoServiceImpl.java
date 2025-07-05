package com.shixun.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shixun.dao.EmploymentInfoDao;
import com.shixun.entity.EmploymentInfo;
import com.shixun.service.EmploymentInfoService;
import org.springframework.stereotype.Service;

/**
 * (EmploymentInfo)表服务实现类
 *
 * @author makejava
 * @since 2025-06-30 18:45:11
 */
@Service("employmentInfoService")
public class EmploymentInfoServiceImpl extends ServiceImpl<EmploymentInfoDao, EmploymentInfo> implements EmploymentInfoService {

}

