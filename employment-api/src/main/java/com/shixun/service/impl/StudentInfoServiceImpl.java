package com.shixun.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shixun.controller.vo.EducationRateVO;
import com.shixun.dao.StudentInfoDao;
import com.shixun.dao.SummaryOverviewDao;
import com.shixun.entity.StudentInfo;
import com.shixun.entity.SummaryOverview;
import com.shixun.service.StudentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * (StudentInfo)表服务实现类
 *
 * @author makejava
 * @since 2025-06-30 18:47:06
 */
@Service("studentInfoService")
public class StudentInfoServiceImpl extends ServiceImpl<StudentInfoDao, StudentInfo> implements StudentInfoService {

    @Autowired
    SummaryOverviewDao summaryOverviewDao;
    @Override
    public List<EducationRateVO> getEducationRate() {
        List<SummaryOverview> educationRate = summaryOverviewDao.getEducationRate();
        List<EducationRateVO> resultList = new ArrayList<>();

        // 计算总人数
        int totalStudents = 0;
        if (educationRate != null && !educationRate.isEmpty()) {
            for (SummaryOverview overview : educationRate) {
                if (overview.getTotalStudents() != null) {
                    totalStudents += overview.getTotalStudents();
                }
            }

            // 计算各学历占比
            for (SummaryOverview overview : educationRate) {
                EducationRateVO vo = new EducationRateVO();
                vo.setEducationLevel(overview.getEducationLevel());
                vo.setTotalLevelStudents(overview.getTotalStudents());

                // 计算占比 (保留两位小数)
                if (overview.getTotalStudents() != null && totalStudents > 0) {
                    double rate = (double) overview.getTotalStudents() / totalStudents * 100;
                    vo.setRate((double) Math.round(rate * 100) / 100); // 保留两位小数
                }

                vo.setStatDate(overview.getStatDate());
                resultList.add(vo);
            }
        }

        return resultList;
    }
}

