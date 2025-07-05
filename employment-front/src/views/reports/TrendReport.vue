<template>
  <div class="trend-report-container">
    <div class="trend-content-scroll">
      <el-row :gutter="20" class="mb-20">
        <el-col :span="24">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>趋势分析报告</span>
                <el-button-group>
                  <el-button type="primary" @click="generateTrendReport">
                    <el-icon><TrendCharts /></el-icon>
                    生成趋势报告
                  </el-button>
                  <el-button @click="exportTrendData">
                    <el-icon><Download /></el-icon>
                    导出数据
                  </el-button>
                </el-button-group>
              </div>
            </template>

            <el-row :gutter="20">
              <el-col :span="8">
                <el-card class="trend-card">
                  <div class="trend-content">
                    <div class="trend-icon positive">
                      <el-icon><TrendCharts /></el-icon>
                    </div>
                    <div class="trend-info">
                      <div class="trend-value">+1.4%</div>
                      <div class="trend-label">就业率增长</div>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card class="trend-card">
                  <div class="trend-content">
                    <div class="trend-icon positive">
                      <el-icon><Money /></el-icon>
                    </div>
                    <div class="trend-info">
                      <div class="trend-value">+8.2%</div>
                      <div class="trend-label">薪资增长</div>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card class="trend-card">
                  <div class="trend-content">
                    <div class="trend-icon negative">
                      <el-icon><User /></el-icon>
                    </div>
                    <div class="trend-info">
                      <div class="trend-value">-2.1%</div>
                      <div class="trend-label">人才缺口</div>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="mb-20">
        <el-col :span="24">
          <el-card>
            <template #header>
              <span>趋势分析概览</span>
            </template>

            <div class="trend-analysis">
              <h3>2024年大学生就业趋势分析</h3>

              <div class="analysis-section">
                <h4>1. 整体就业趋势</h4>
                <p>2024年大学生就业市场呈现稳中向好的发展态势。就业率持续上升，从2023年的89.1%增长至90.5%，增幅1.4个百分点。这一增长主要得益于国家政策支持、经济复苏以及新兴产业的快速发展。</p>

                <div class="trend-highlights">
                  <el-tag type="success" class="trend-tag">就业率创新高</el-tag>
                  <el-tag type="warning" class="trend-tag">结构性矛盾仍存</el-tag>
                  <el-tag type="info" class="trend-tag">新兴行业需求旺盛</el-tag>
                </div>
              </div>

              <div class="analysis-section">
                <h4>2. 薪资水平趋势</h4>
                <p>2024年大学生平均起薪较去年同期上涨8.2%，达到11,200元。其中，技术类岗位薪资涨幅最为明显，人工智能、大数据、云计算等新兴技术领域平均薪资超过14,000元。</p>

                <el-row :gutter="20" class="salary-trends">
                  <el-col :span="6">
                    <div class="salary-item">
                      <div class="salary-value">14,200</div>
                      <div class="salary-label">人工智能</div>
                      <div class="salary-trend up">↑12.5%</div>
                    </div>
                  </el-col>
                  <el-col :span="6">
                    <div class="salary-item">
                      <div class="salary-value">13,500</div>
                      <div class="salary-label">大数据</div>
                      <div class="salary-trend up">↑10.8%</div>
                    </div>
                  </el-col>
                  <el-col :span="6">
                    <div class="salary-item">
                      <div class="salary-value">12,800</div>
                      <div class="salary-label">计算机</div>
                      <div class="salary-trend up">↑9.2%</div>
                    </div>
                  </el-col>
                  <el-col :span="6">
                    <div class="salary-item">
                      <div class="salary-value">9,800</div>
                      <div class="salary-label">金融</div>
                      <div class="salary-trend up">↑5.1%</div>
                    </div>
                  </el-col>
                </el-row>
              </div>

              <div class="analysis-section">
                <h4>3. 行业发展趋势</h4>
                <p>新兴行业继续保持强劲增长态势，传统行业加速数字化转型。IT/互联网行业仍是就业首选，占比28.5%；医疗健康行业增长最快，增幅达15.7%。</p>

                <div class="industry-trends">
                  <div class="industry-item hot">
                    <span class="industry-name">医疗健康</span>
                    <span class="industry-growth">+15.7%</span>
                  </div>
                  <div class="industry-item hot">
                    <span class="industry-name">IT/互联网</span>
                    <span class="industry-growth">+12.3%</span>
                  </div>
                  <div class="industry-item">
                    <span class="industry-name">咨询服务</span>
                    <span class="industry-growth">+9.5%</span>
                  </div>
                  <div class="industry-item">
                    <span class="industry-name">教育</span>
                    <span class="industry-growth">+8.1%</span>
                  </div>
                  <div class="industry-item cold">
                    <span class="industry-name">房地产</span>
                    <span class="industry-growth">-2.1%</span>
                  </div>
                </div>
              </div>

              <div class="analysis-section">
                <h4>4. 地区发展趋势</h4>
                <p>一线城市就业机会依然集中，但新一线城市吸引力显著提升。杭州、成都、武汉等城市就业增长率超过10%，人才政策效果显著。</p>
              </div>

              <div class="analysis-section">
                <h4>5. 未来展望</h4>
                <p>预计2025年大学生就业将继续保持稳定增长态势，新兴技术岗位需求将进一步增加，传统行业数字化转型将创造更多就业机会。建议毕业生关注技能提升和终身学习。</p>

                <div class="future-predictions">
                  <el-alert
                    title="预测：2025年就业率有望突破92%"
                    type="success"
                    :closable="false"
                    show-icon
                  />
                  <el-alert
                    title="趋势：技术类岗位需求将持续增长"
                    type="info"
                    :closable="false"
                    show-icon
                  />
                  <el-alert
                    title="建议：加强新技术技能培训"
                    type="warning"
                    :closable="false"
                    show-icon
                  />
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-card>
            <template #header>
              <span>趋势报告历史</span>
            </template>

            <el-table :data="trendReports" stripe>
              <el-table-column prop="title" label="报告标题" />
              <el-table-column prop="period" label="分析周期" />
              <el-table-column prop="createTime" label="生成时间" />
              <el-table-column prop="status" label="状态">
                <template #default="{ row }">
                  <el-tag :type="row.status === '已完成' ? 'success' : 'warning'">
                    {{ row.status }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作">
                <template #default="{ row }">
                  <el-button size="small" @click="viewReport(row)">查看</el-button>
                  <el-button size="small" @click="downloadTrendReport(row)">下载</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

// 趋势报告历史
const trendReports = ref([
  {
    id: 1,
    title: '2024年第一季度就业趋势分析',
    period: '2024.01-2024.03',
    createTime: '2024-01-15 16:30:00',
    status: '已完成'
  },
  {
    id: 2,
    title: '2023年度就业趋势总结',
    period: '2023.01-2023.12',
    createTime: '2024-01-01 10:00:00',
    status: '已完成'
  },
  {
    id: 3,
    title: '2023年第四季度趋势分析',
    period: '2023.10-2023.12',
    createTime: '2023-12-31 14:20:00',
    status: '已完成'
  }
])

// 生成趋势报告
const generateTrendReport = () => {
  ElMessage.success('正在生成趋势分析报告...')
}

// 导出趋势数据
const exportTrendData = () => {
  ElMessage.success('正在导出趋势数据...')
}

// 查看报告
const viewReport = (row: any) => {
  ElMessage.info(`正在打开报告：${row.title}`)
}

// 下载趋势报告
const downloadTrendReport = (row: any) => {
  ElMessage.success(`正在下载：${row.title}`)
}
</script>

<style scoped>
.trend-report-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  padding: 20px;
  gap: 20px;
  overflow: auto;
}
.trend-content-scroll {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.filter-row {
  display: flex;
  gap: 20px;
  align-items: center;
}

.chart-row {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  min-height: 0;
}

.chart-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chart-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 15px;
}

.chart {
  flex: 1;
  width: 100%;
  height: 100%;
  min-height: 0;
}

.data-table {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.table-title {
  font-size: 16px;
  font-weight: bold;
}

.table-content {
  flex: 1;
  overflow: auto;
}

.mb-20 {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.trend-card {
  height: 120px;
}

.trend-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.trend-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 24px;
  color: white;
}

.trend-icon.positive {
  background: linear-gradient(135deg, #67C23A 0%, #85CE61 100%);
}

.trend-icon.negative {
  background: linear-gradient(135deg, #F56C6C 0%, #F78989 100%);
}

.trend-info {
  flex: 1;
}

.trend-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.trend-label {
  font-size: 14px;
  color: #909399;
}

.trend-analysis {
  padding: 20px;
}

.trend-analysis h3 {
  color: #409EFF;
  margin-bottom: 20px;
  text-align: center;
}

.analysis-section {
  margin-bottom: 30px;
}

.analysis-section h4 {
  color: #303133;
  margin-bottom: 15px;
  font-size: 16px;
}

.analysis-section p {
  line-height: 1.8;
  color: #606266;
  margin-bottom: 15px;
}

.trend-highlights {
  margin: 15px 0;
}

.trend-tag {
  margin-right: 10px;
}

.salary-trends {
  margin: 20px 0;
}

.salary-item {
  text-align: center;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.salary-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 8px;
}

.salary-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 5px;
}

.salary-trend {
  font-size: 12px;
  font-weight: bold;
}

.salary-trend.up {
  color: #67C23A;
}

.industry-trends {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin: 20px 0;
}

.industry-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  background-color: #f0f2f5;
  border-radius: 6px;
  min-width: 140px;
}

.industry-item.hot {
  background-color: #f0f9ff;
  border: 1px solid #409EFF;
}

.industry-item.cold {
  background-color: #fef0f0;
  border: 1px solid #F56C6C;
}

.industry-name {
  font-weight: 500;
  color: #303133;
}

.industry-growth {
  font-weight: bold;
  color: #67C23A;
}

.industry-item.cold .industry-growth {
  color: #F56C6C;
}

.future-predictions {
  margin: 20px 0;
}

.future-predictions .el-alert {
  margin-bottom: 10px;
}

:deep(.el-card__body) {
  padding: 20px;
}

:deep(.el-card__header) {
  padding: 18px 20px;
  border-bottom: 1px solid #ebeef5;
}
</style>
