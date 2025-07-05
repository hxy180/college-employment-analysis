<template>
  <div class="help-container">
    <div class="help-content">
      <el-row :gutter="20">
        <el-col :span="24">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>帮助中心</span>
              </div>
            </template>

            <el-collapse v-model="activeNames">
              <el-collapse-item title="如何使用数据概览功能？" name="1">
                <div>
                  数据概览页面展示了大学生就业的关键指标，包括：
                  <ul>
                    <li>最新就业率统计</li>
                    <li>毕业生总数</li>
                    <li>平均薪资水平</li>
                    <li>就业趋势图表</li>
                  </ul>
                  您可以通过点击刷新按钮来获取最新数据。
                </div>
              </el-collapse-item>

              <el-collapse-item title="如何进行薪资分析？" name="2">
                <div>
                  薪资分析功能提供了详细的薪资数据分析：
                  <ul>
                    <li>可按专业、地区、薪资范围进行筛选</li>
                    <li>提供平均薪资、中位数薪资等统计指标</li>
                    <li>支持数据导出功能</li>
                    <li>包含多种图表展示方式</li>
                  </ul>
                </div>
              </el-collapse-item>

              <el-collapse-item title="行业分析有哪些功能？" name="3">
                <div>
                  行业分析模块帮助您了解各行业就业情况：
                  <ul>
                    <li>行业就业分布饼图</li>
                    <li>行业增长率对比</li>
                    <li>新兴行业发展趋势</li>
                    <li>行业详细信息查看</li>
                  </ul>
                </div>
              </el-collapse-item>

              <el-collapse-item title="地区分析功能说明" name="4">
                <div>
                  地区分析为您提供全国各地区就业数据：
                  <ul>
                    <li>地区就业率排名</li>
                    <li>平均薪资地区对比</li>
                    <li>岗位数量分布</li>
                    <li>地区对比功能</li>
                  </ul>
                </div>
              </el-collapse-item>

              <el-collapse-item title="如何生成和下载报告？" name="5">
                <div>
                  报告功能让您轻松获取分析结果：
                  <ul>
                    <li>支持就业报告和趋势报告生成</li>
                    <li>可选择不同的报告格式</li>
                    <li>支持PDF和Excel格式导出</li>
                    <li>可自定义报告内容</li>
                  </ul>
                </div>
              </el-collapse-item>
            </el-collapse>

            <div class="contact-info">
              <el-divider content-position="left">联系我们</el-divider>
              <p>如果您有其他问题，请通过以下方式联系我们：</p>
              <ul>
                <li>邮箱：houxiaoyu0106@qq.com</li>
                <!-- <li>电话：400-123-4567</li>
                <li>工作时间：周一至周五 9:00-18:00</li> -->
              </ul>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const activeNames = ref(['1'])
</script>

<style scoped>
:root, html, body, #app {
  width: 100vw;
  min-height: 100vh;
  margin: 0;
  padding: 0;
}
.main-container, .help-wrapper, .help-content {
  width: 100vw !important;
  min-height: 100vh !important;
  margin: 0 !important;
  padding: 0 !important;
  box-sizing: border-box;
}

.help-container {
  height: 100%;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow: hidden;
}

.help-content {
  flex: 1;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
  overflow: auto;
}

.help-section {
  margin-bottom: 30px;
}

.help-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #409EFF;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  margin: 15px 0;
  color: #303133;
}

.help-text {
  font-size: 14px;
  line-height: 1.6;
  color: #606266;
  margin: 10px 0;
}

.help-list {
  list-style: disc;
  padding-left: 20px;
  margin: 10px 0;
}

.help-list-item {
  margin: 5px 0;
  color: #606266;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.contact-info {
  margin-top: 30px;
}

.contact-info ul {
  margin-left: 20px;
}

.contact-info li {
  margin: 8px 0;
}

:deep(.el-collapse-item__header) {
  font-weight: 500;
}

:deep(.el-collapse-item__content) {
  padding: 15px 20px;
  line-height: 1.6;
}
</style>
