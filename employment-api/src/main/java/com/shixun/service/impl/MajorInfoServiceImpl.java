package com.shixun.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shixun.dao.MajorInfoDao;
import com.shixun.entity.MajorInfo;
import com.shixun.service.MajorInfoService;
import org.springframework.stereotype.Service;

/**
 * (MajorInfo)表服务实现类
 *
 * @author makejava
 * @since 2025-06-30 18:45:47
 */
@Service("majorInfoService")
public class MajorInfoServiceImpl extends ServiceImpl<MajorInfoDao, MajorInfo> implements MajorInfoService {

}

