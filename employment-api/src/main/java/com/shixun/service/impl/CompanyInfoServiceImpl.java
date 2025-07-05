package com.shixun.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shixun.dao.CompanyInfoDao;
import com.shixun.entity.CompanyInfo;
import com.shixun.service.CompanyInfoService;
import org.springframework.stereotype.Service;

/**
 * (CompanyInfo)表服务实现类
 *
 * @author makejava
 * @since 2025-06-30 18:34:30
 */
@Service("companyInfoService")
public class CompanyInfoServiceImpl extends ServiceImpl<CompanyInfoDao, CompanyInfo> implements CompanyInfoService {

}

