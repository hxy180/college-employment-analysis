<template>
  <div class="employment-report-container">
    <div class="employment-content-scroll">
      <el-row :gutter="20" class="mb-20">
        <el-col :span="24">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>就业报告生成</span>
                <el-button type="primary" @click="generateReport">
                  <el-icon><Document /></el-icon>
                  生成报告
                </el-button>
              </div>
            </template>

            <el-form :model="reportConfig" label-width="120px">
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="报告类型">
                    <el-select v-model="reportConfig.type" placeholder="选择报告类型">
                      <el-option label="综合就业报告" value="comprehensive" />
                      <el-option label="专业就业报告" value="major" />
                      <el-option label="地区就业报告" value="region" />
                      <el-option label="行业就业报告" value="industry" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="时间范围">
                    <el-date-picker
                      v-model="reportConfig.dateRange"
                      type="daterange"
                      range-separator="至"
                      start-placeholder="开始日期"
                      end-placeholder="结束日期"
                      format="YYYY-MM-DD"
                      value-format="YYYY-MM-DD"
                    />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="输出格式">
                    <el-radio-group v-model="reportConfig.format">
                      <el-radio value="pdf">PDF</el-radio>
                      <el-radio value="excel">Excel</el-radio>
                      <el-radio value="word">Word</el-radio>
                    </el-radio-group>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="包含图表">
                    <el-switch v-model="reportConfig.includeCharts" />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="mb-20">
        <el-col :span="24">
          <el-card>
            <template #header>
              <span>报告预览</span>
            </template>

            <div class="report-preview">
              <div class="report-header">
                <h2>{{ getReportTitle() }}</h2>
                <p class="report-date">生成时间：{{ new Date().toLocaleString() }}</p>
              </div>

              <div class="report-summary">
                <h3>执行摘要</h3>
                <p>本报告基于最新的大学生就业数据，深入分析了当前就业市场的整体状况、主要趋势和关键指标。</p>

                <div class="key-findings">
                  <h4>主要发现：</h4>
                  <ul>
                    <li>2024年大学生就业率达到90.5%，较去年同期上升1.4个百分点</li>
                    <li>IT/互联网行业仍是毕业生就业的主要选择，占比28.5%</li>
                    <li>一线城市就业率最高，北京达到94.2%</li>
                    <li>人工智能专业平均薪资最高，达到14,200元</li>
                  </ul>
                </div>
              </div>

              <div class="report-charts" v-if="reportConfig.includeCharts">
                <h3>数据图表</h3>
                <el-row :gutter="20">
                  <el-col :span="12">
                    <div class="chart-placeholder">
                      <p>就业率趋势图</p>
                    </div>
                  </el-col>
                  <el-col :span="12">
                    <div class="chart-placeholder">
                      <p>行业分布图</p>
                    </div>
                  </el-col>
                </el-row>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-card>
            <template #header>
              <span>历史报告</span>
            </template>

            <el-table :data="historyReports" stripe>
              <el-table-column prop="name" label="报告名称" />
              <el-table-column prop="type" label="类型">
                <template #default="{ row }">
                  <el-tag>{{ getReportTypeLabel(row.type) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="生成时间" />
              <el-table-column prop="format" label="格式">
                <template #default="{ row }">
                  <el-tag :type="getFormatType(row.format)">{{ row.format.toUpperCase() }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="size" label="文件大小" />
              <el-table-column label="操作">
                <template #default="{ row }">
                  <el-button size="small" @click="downloadReport(row)">
                    <el-icon><Download /></el-icon>
                    下载
                  </el-button>
                  <el-button size="small" type="danger" @click="deleteReport(row)">
                    <el-icon><Delete /></el-icon>
                    删除
                  </el-button>
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
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 报告配置
const reportConfig = reactive({
  type: 'comprehensive',
  dateRange: [],
  format: 'pdf',
  includeCharts: true
})

// 历史报告
const historyReports = ref([
  {
    id: 1,
    name: '2024年第一季度就业报告',
    type: 'comprehensive',
    createTime: '2024-01-15 14:30:00',
    format: 'pdf',
    size: '2.5MB'
  },
  {
    id: 2,
    name: '计算机专业就业分析',
    type: 'major',
    createTime: '2024-01-10 10:20:00',
    format: 'excel',
    size: '1.8MB'
  },
  {
    id: 3,
    name: '北京地区就业状况',
    type: 'region',
    createTime: '2024-01-05 16:45:00',
    format: 'word',
    size: '3.2MB'
  }
])

// 获取报告标题
const getReportTitle = () => {
  const titleMap: Record<string, string> = {
    comprehensive: '大学生综合就业分析报告',
    major: '专业就业分析报告',
    region: '地区就业分析报告',
    industry: '行业就业分析报告'
  }
  return titleMap[reportConfig.type] || '就业分析报告'
}

// 获取报告类型标签
const getReportTypeLabel = (type: string) => {
  const labelMap: Record<string, string> = {
    comprehensive: '综合报告',
    major: '专业报告',
    region: '地区报告',
    industry: '行业报告'
  }
  return labelMap[type] || type
}

// 获取格式类型
const getFormatType = (format: string) => {
  const typeMap: Record<string, string> = {
    pdf: 'danger',
    excel: 'success',
    word: 'warning'
  }
  return typeMap[format] || 'info'
}

// 生成报告
const generateReport = () => {
  if (!reportConfig.dateRange || reportConfig.dateRange.length === 0) {
    ElMessage.warning('请选择时间范围')
    return
  }

  ElMessage.success('报告生成中，请稍候...')

  // 模拟报告生成过程
  setTimeout(() => {
    ElMessage.success('报告生成完成！')
  }, 2000)
}

// 下载报告
const downloadReport = (row: any) => {
  ElMessage.success(`正在下载 ${row.name}...`)
}

// 删除报告
const deleteReport = (row: any) => {
  ElMessageBox.confirm(`确定要删除报告 ${row.name} 吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('报告已删除')
  })
}
</script>

<style scoped>
.employment-report-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  padding: 20px;
  gap: 20px;
  overflow: auto;
}

.employment-content-scroll {
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

.employment-report {
  padding: 20px;
}

.mb-20 {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.report-preview {
  padding: 20px;
  background-color: #fafafa;
  border-radius: 6px;
}

.report-header {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #e9ecef;
}

.report-header h2 {
  color: #303133;
  margin-bottom: 10px;
}

.report-date {
  color: #909399;
  font-size: 14px;
}

.report-summary {
  margin-bottom: 30px;
}

.report-summary h3 {
  color: #409EFF;
  margin-bottom: 15px;
}

.report-summary p {
  line-height: 1.6;
  color: #606266;
  margin-bottom: 20px;
}

.key-findings h4 {
  color: #E6A23C;
  margin-bottom: 10px;
}

.key-findings ul {
  padding-left: 20px;
}

.key-findings li {
  margin: 8px 0;
  line-height: 1.5;
  color: #606266;
}

.report-charts h3 {
  color: #409EFF;
  margin-bottom: 20px;
}

.chart-placeholder {
  height: 200px;
  background-color: #f5f7fa;
  border: 2px dashed #dcdfe6;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  color: #909399;
  font-size: 16px;
}

:deep(.el-card__body) {
  padding: 20px;
}

:deep(.el-card__header) {
  padding: 18px 20px;
  border-bottom: 1px solid #ebeef5;
}

:deep(.el-select) {
  width: 100%;
}

:deep(.el-date-editor) {
  width: 100%;
}
</style>
