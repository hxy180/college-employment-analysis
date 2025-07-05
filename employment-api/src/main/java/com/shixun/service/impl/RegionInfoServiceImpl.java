package com.shixun.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shixun.dao.RegionInfoDao;
import com.shixun.entity.RegionInfo;
import com.shixun.service.RegionInfoService;
import org.springframework.stereotype.Service;

/**
 * (RegionInfo)表服务实现类
 *
 * @author makejava
 * @since 2025-06-30 18:46:15
 */
@Service("regionInfoService")
public class RegionInfoServiceImpl extends ServiceImpl<RegionInfoDao, RegionInfo> implements RegionInfoService {

}

