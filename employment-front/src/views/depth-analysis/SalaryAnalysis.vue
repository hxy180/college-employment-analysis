<template>
  <div class="salary-analysis-container" style="width: 100%; padding: 10px; margin: 0;">
    <el-row :gutter="20" class="mb-20">
      <el-col :span="12">
        <el-card class="chart-container">
          <template #header>
            <span class="chart-title">专业薪资分布</span>
          </template>
          <v-chart
            class="chart"
            :option="majorSalaryOption"
            :loading="loading"
            autoresize
          />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-container">
          <template #header>
            <div style="display: flex; align-items: center; justify-content: space-between;">
              <span class="chart-title">薪资区间分布</span>
              <el-button-group>
                <el-button :type="gradCode === 1 ? 'primary' : 'default'" @click="gradCode = 1">应届生</el-button>
                <el-button :type="gradCode === 0 ? 'primary' : 'default'" @click="gradCode = 0">非应届</el-button>
              </el-button-group>
            </div>
          </template>
          <v-chart
            class="chart"
            :option="salaryDistributionOption"
            :loading="loading"
            autoresize
          />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mb-20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>年平均薪资与就业数量散点图</span>
          </template>
          <v-chart
            class="chart-large"
            :option="salaryScatterOption"
            :loading="loading"
            autoresize
          />
        </el-card>
      </el-col>
    </el-row>

    <!-- <el-row :gutter="20" class="mb-20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>就业数量TOP10专业</span>
          </template>
          <v-chart
            class="chart-large"
            :option="top10EmploymentOption"
            :loading="loading"
            autoresize
          />
        </el-card>
      </el-col>
    </el-row> -->

    <el-row :gutter="20" class="mb-20">
      <el-col :span="12">
        <el-card class="chart-container">
          <template #header>
            <span class="chart-title">不同学历薪资对比</span>
          </template>
          <v-chart
            class="chart"
            :option="educationSalaryOption"
            :loading="loading"
            autoresize
          />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-container">
          <template #header>
            <span class="chart-title">薪资增长趋势</span>
          </template>
          <v-chart
            class="chart"
            :option="salaryTrendOption"
            :loading="loading"
            autoresize
          />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="data-table">
          <template #header>
            <div class="table-header">
              <span class="table-title">详细数据表格</span>
              <el-input
                v-model="searchText"
                placeholder="搜索专业..."
                style="width: 200px"
                clearable
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
            </div>
          </template>
          <div class="table-content">
            <el-table :data="filteredTableData" stripe>
              <el-table-column prop="major" label="专业" sortable />
              <el-table-column prop="avgSalary" label="平均薪资" sortable>
                <template #default="{ row }">
                  ¥{{ row.avgSalary.toLocaleString() }}
                </template>
              </el-table-column>
              <el-table-column prop="minSalary" label="最低薪资" sortable>
                <template #default="{ row }">
                  ¥{{ row.minSalary.toLocaleString() }}
                </template>
              </el-table-column>
              <el-table-column prop="maxSalary" label="最高薪资" sortable>
                <template #default="{ row }">
                  ¥{{ row.maxSalary.toLocaleString() }}
                </template>
              </el-table-column>
              <el-table-column prop="count" label="样本数量" sortable>
                <template #default="{ row }">
                  {{ row.count.toLocaleString() }}
                </template>
              </el-table-column>
              <el-table-column label="薪资范围">
                <template #default="{ row }">
                  <el-tag>{{ row.maxSalary - row.minSalary }}K</el-tag>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useEmploymentStore } from '@/stores/employment'
import VChart from 'vue-echarts'
import * as echarts from 'echarts/core'
import { use } from 'echarts/core'
import { PieChart, BarChart, LineChart, ScatterChart, MapChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent, VisualMapComponent, DataZoomComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import type { ComposeOption } from 'echarts/core'
import type { MapSeriesOption } from 'echarts/charts'

// 注册ECharts组件
use([PieChart, BarChart, LineChart, ScatterChart, MapChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent, VisualMapComponent, DataZoomComponent, CanvasRenderer])
import chinaJson from '@/assets/china.json'
import { ElMessage } from 'element-plus'

const employmentStore = useEmploymentStore()
const loading = ref(false)
const selectedMajor = ref('')
const selectedRegion = ref('')
const salaryRange = ref('')
const searchText = ref('')

// 新增：专业-行业-薪资数据
const majorIndustrySalaryData = ref<any[]>([])

// 新增：应届/非应届切换
const gradCode = ref(1) // 1=应届，0=非应届
const salaryRangeData = ref<any[]>([])

// 新增：学历薪资数据
const educationSalaryData = ref<any[]>([])

// 计算属性
const majors = computed(() =>
  employmentStore.salaryData.map(item => item.major)
)

const regions = computed(() =>
  employmentStore.regionData.map(item => item.region)
)

const filteredData = computed(() => {
  let data = [...employmentStore.salaryData]

  if (selectedMajor.value) {
    data = data.filter(item => item.major === selectedMajor.value)
  }

  if (salaryRange.value) {
    const [min, max] = salaryRange.value.split('-').map(Number)
    data = data.filter(item => item.avgSalary >= min && item.avgSalary <= max)
  }

  return data
})

const filteredAverageSalary = computed(() => {
  if (filteredData.value.length === 0) return 0
  const total = filteredData.value.reduce((sum, item) => sum + item.avgSalary * item.count, 0)
  const totalCount = filteredData.value.reduce((sum, item) => sum + item.count, 0)
  return Math.round(total / totalCount)
})

const medianSalary = computed(() => {
  if (filteredData.value.length === 0) return 0
  const sorted = [...filteredData.value].sort((a, b) => a.avgSalary - b.avgSalary)
  const mid = Math.floor(sorted.length / 2)
  return sorted.length % 2 === 0
    ? Math.round((sorted[mid - 1].avgSalary + sorted[mid].avgSalary) / 2)
    : sorted[mid].avgSalary
})

const totalSamples = computed(() =>
  filteredData.value.reduce((sum, item) => sum + item.count, 0)
)

const filteredTableData = computed(() => {
  let data = filteredData.value
  if (searchText.value) {
    data = data.filter(item =>
      item.major.toLowerCase().includes(searchText.value.toLowerCase())
    )
  }
  return data
})

// 获取薪资区间分布数据
const fetchSalaryRangeData = () => {
  loading.value = true
  fetch(`http://localhost:9908/api/summarySalaryRange/salaryRange?gradCode=${gradCode.value}`)
    .then(res => {
      if (!res.ok) throw new Error('网络响应失败');
      return res.text();
    })
    .then(text => {
      if (!text) throw new Error('接口无数据返回');
      try {
        return JSON.parse(text);
      } catch (e) {
        throw new Error('返回内容不是有效JSON');
      }
    })
    .then(data => {
      if (data.code === 200) {
        salaryRangeData.value = data.data
      } else {
        throw new Error(data.msg || '接口返回异常');
      }
    })
    .catch(err => {
      console.error('获取薪资区间分布数据失败:', err.message);
    })
    .finally(() => {
      loading.value = false
    })
}

// 获取学历薪资数据
const fetchEducationSalaryData = () => {
  fetch('http://localhost:9908/api/summarySalaryRange/educationSalaryRange')
    .then(res => {
      if (!res.ok) throw new Error('网络响应失败');
      return res.text();
    })
    .then(text => {
      if (!text) throw new Error('接口无数据返回');
      try {
        return JSON.parse(text);
      } catch (e) {
        throw new Error('返回内容不是有效JSON');
      }
    })
    .then(data => {
      if (data.code === 200) {
        educationSalaryData.value = data.data
      } else {
        throw new Error(data.msg || '接口返回异常');
      }
    })
    .catch(err => {
      console.error('获取学历薪资数据失败:', err.message);
    })
}

// 页面加载时获取
onMounted(() => {
  echarts.registerMap('china', chinaJson as any)
  fetch('http://localhost:9908/api/summarySalaryRange/industrySalaryRange?type=Top')
    .then(res => {
      if (!res.ok) throw new Error('网络响应失败');
      return res.text();
    })
    .then(text => {
      if (!text) throw new Error('接口无数据返回');
      try {
        return JSON.parse(text);
      } catch (e) {
        throw new Error('返回内容不是有效JSON');
      }
    })
    .then(data => {
      if (data.code === 200) {
        majorIndustrySalaryData.value = data.data
      } else {
        throw new Error(data.msg || '接口返回异常');
      }
    })
    .catch(err => {
      console.error('获取专业-行业薪资数据失败:', err.message);
    });
  fetchSalaryRangeData()
  fetchEducationSalaryData()
})

// watch 切换应届/非应届时自动请求
watch(gradCode, fetchSalaryRangeData)

// 专业薪资分布图配置（用接口数据）
const majorSalaryOption = computed(() => {
  // 取前8个专业-行业组合，按薪资降序
  const sorted = [...majorIndustrySalaryData.value].sort((a, b) => b.avgSalary - a.avgSalary).slice(0, 10)
  return {
    title: {
      text: '专业-行业平均薪资排名',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      formatter: (params: any) => {
        const data = params[0]
        return `${data.name}<br/>平均薪资: ¥${data.value.toLocaleString()}`
      }
    },
    xAxis: {
      type: 'category',
      data: sorted.map(item => `${item.majorName}`),
      axisLabel: {
        rotate: 45,
        interval: 0
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '¥{value}'
      }
    },
    series: [{
      data: sorted.map(item => Math.round(item.avgSalary)),
      type: 'bar',
      itemStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [{
            offset: 0, color: '#409EFF'
          }, {
            offset: 1, color: '#79BBFF'
          }]
        }
      }
    }]
  }
})

// 薪资区间分布图配置（用接口数据）
const salaryDistributionOption = computed(() => {
  return {
    title: {
      text: '薪资区间人数分布',
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
        const data = salaryRangeData.value[params.dataIndex]
        return `${data.salary_range}: ${data.rate}`
      }
    },
    series: [{
      name: '薪资分布',
      type: 'pie',
      radius: '70%',
      data: salaryRangeData.value.map(item => ({
        name: item.salary_range,
        value: parseFloat(item.rate),
        itemStyle: { color: item.color }
      })),
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }
})

// 薪资散点图配置
const salaryScatterOption = computed(() => ({
  title: {
    text: '专业年平均薪资与就业数量关系',
    left: 'center'
  },
  tooltip: {
    trigger: 'item',
    formatter: (params: any) => {
      const data = filteredData.value[params.dataIndex]
      return `${data.major}<br/>平均薪资: ¥${data.avgSalary.toLocaleString()}<br/>就业人数: ${data.count.toLocaleString()}`
    }
  },
  xAxis: {
    type: 'value',
    name: '年平均薪资 (元)',
    nameLocation: 'middle',
    nameGap: 30,
    axisLabel: {
      formatter: '¥{value}'
    }
  },
  yAxis: {
    type: 'value',
    name: '就业人数',
    nameLocation: 'middle',
    nameGap: 50,
    axisLabel: {
      formatter: '{value}'
    }
  },
  series: [{
    symbolSize: (data: any) => Math.sqrt(data[1]) / 20,
    data: filteredData.value.map(item => [item.avgSalary, item.count]),
    type: 'scatter',
    itemStyle: {
      color: '#409EFF',
      opacity: 0.7
    }
  }]
}))

// 学历薪资对比图表 option
const educationSalaryOption = computed(() => {
  return {
    title: {
      text: '不同学历薪资对比',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      formatter: (params: any) => {
        const data = params[0]
        return `${data.name}<br/>平均薪资: ¥${data.value.toLocaleString()}`
      }
    },
    xAxis: {
      type: 'category',
      data: educationSalaryData.value.map(item => item.education),
      axisLabel: {
        rotate: 0
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '¥{value}'
      }
    },
    series: [{
      data: educationSalaryData.value.map(item => Math.round(item.avgSalary)),
      type: 'bar',
      itemStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [{
            offset: 0, color: '#36cfc9'
          }, {
            offset: 1, color: '#5cdbd3'
          }]
        }
      }
    }]
  }
})

// 薪资增长趋势图配置
const salaryTrendOption = computed(() => {
  // 模拟薪资增长数据
  const trendData = [
    { year: 2019, salary: 8500 },
    { year: 2020, salary: 8800 },
    { year: 2021, salary: 9200 },
    { year: 2022, salary: 9800 },
    { year: 2023, salary: 10500 },
    { year: 2024, salary: 11200 }
  ]

  return {
    title: {
      text: '平均薪资增长趋势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      formatter: (params: any) => {
        const data = params[0]
        return `${data.name}年<br/>平均薪资: ¥${data.value.toLocaleString()}`
      }
    },
    xAxis: {
      type: 'category',
      data: trendData.map(item => item.year)
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '¥{value}'
      }
    },
    series: [{
      data: trendData.map(item => item.salary),
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: {
        width: 3,
        color: '#E6A23C'
      },
      itemStyle: {
        color: '#E6A23C'
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [{
            offset: 0, color: 'rgba(230, 162, 60, 0.3)'
          }, {
            offset: 1, color: 'rgba(230, 162, 60, 0.1)'
          }]
        }
      }
    }]
  }
})

// 地图视图类型
const mapViewType = ref<'average' | 'growth' | 'count'>('average')

// 地图配置
const salaryMapOption = computed(() => {
  const mapData = [
    { name: '北京', value: 15000, growth: 12.5, count: 25000 },
    { name: '上海', value: 14500, growth: 11.8, count: 22000 },
    { name: '广东', value: 13000, growth: 10.5, count: 20000 },
    { name: '浙江', value: 12500, growth: 9.8, count: 18000 },
    { name: '江苏', value: 12000, growth: 9.2, count: 17000 },
    // ... 其他省份数据
  ]

  const visualMapConfig = {
    average: {
      min: 5000,
      max: 15000,
      text: ['高', '低'] as [string, string],
      calculable: true,
      inRange: {
        color: ['#C6FFDD', '#FBD786', '#f7797d']
      }
    },
    growth: {
      min: 0,
      max: 15,
      text: ['高', '低'] as [string, string],
      calculable: true,
      inRange: {
        color: ['#EECDA3', '#EF629F']
      }
    },
    count: {
      min: 0,
      max: 30000,
      text: ['高', '低'] as [string, string],
      calculable: true,
      inRange: {
        color: ['#8EC5FC', '#E0C3FC']
      }
    }
  } as const

  const dataMapping = {
    average: mapData.map(item => ({ name: item.name, value: item.value })),
    growth: mapData.map(item => ({ name: item.name, value: item.growth })),
    count: mapData.map(item => ({ name: item.name, value: item.count }))
  } as const

  const titleText = {
    average: '各地区平均薪资（元/月）',
    growth: '各地区薪资增长率（%）',
    count: '各地区就业人数（人）'
  } as const

  return {
    title: {
      text: titleText[mapViewType.value],
      left: 'center',
      textStyle: {
        color: '#666'
      }
    },
    tooltip: {
        trigger: 'item',
        formatter: (params: any) => {
          const data = mapData.find(item => item.name === params.name)
          if (!data) return params.name
          return `${params.name}<br/>
                  平均薪资：${data.value}元/月<br/>
                  增长率：${data.growth}%<br/>
                  就业人数：${data.count}人`
        }
      },
    visualMap: {
      type: 'continuous',
      left: 'left',
      top: 'bottom',
      ...visualMapConfig[mapViewType.value]
    },
    geo: {
      map: 'china',
      roam: true,
      scaleLimit: {
        min: 1,
        max: 5
      },
      label: {
        show: true,
        fontSize: 8
      },
      itemStyle: {
        areaColor: '#fff',
        borderColor: '#ccc'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 12,
          color: '#fff'
        },
        itemStyle: {
          areaColor: '#409EFF',
          shadowColor: 'rgba(0, 0, 0, 0.5)',
          shadowBlur: 10
        }
      }
    },
    series: [
      {
        name: titleText[mapViewType.value],
        type: 'map',
        map: 'china',
        data: dataMapping[mapViewType.value]
      }
    ]
  }
})

// 监听地图视图类型变化
watch(mapViewType, () => {
  // 可以在这里添加额外的数据处理逻辑
})

// 更新图表
const updateCharts = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 500)
}

// 导出数据
const exportData = () => {
  ElMessage.success('数据导出功能开发中...')
}

const top10EmploymentMajors = computed(() =>
  filteredData.value
    .slice()
    .sort((a, b) => b.count - a.count)
    .slice(0, 10)
)

// const top10EmploymentOption = computed(() => ({
//   title: {
//     text: '就业数量TOP10专业',
//     left: 'center'
//   },
//   tooltip: {
//     trigger: 'axis'
//   },
//   xAxis: {
//     type: 'category',
//     data: top10EmploymentMajors.value.map(item => item.major),
//     axisLabel: {
//       rotate: 45
//     }
//   },
//   yAxis: {
//     type: 'value',
//     name: '就业人数'
//   },
//   series: [{
//     data: top10EmploymentMajors.value.map(item => item.count),
//     type: 'bar',
//     itemStyle: {
//       color: '#36cfc9'
//     }
//   }]
// }))

onMounted(() => {
  // 页面加载时的初始化逻辑
})
</script>

<style scoped>
:root, html, body, #app {
  width: 100vw;
  min-height: 100vh;
  margin: 0;
  padding: 0;
}
.main-container, .salary-analysis-wrapper, .salary-analysis-content {
  width: 100% !important;
  min-height: 100vh !important;
  margin: 0 !important;
  padding: 0 !important;
  box-sizing: border-box;
}
.salary-analysis-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  overflow: auto;
  min-height: 100vh;
}

.filter-row {
  display: flex;
  gap: 20px;
  align-items: center;
}

.chart-row {
  gap: 20px;
  min-height: 0;
}

@media (max-width: 768px) {
  .chart-row {
    grid-template-columns: 1fr;
  }
}

.chart-container,
.el-card.chart-container {
  min-height: 420px;
  height: 420px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
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

.stat-card {
  height: 120px;
}

.stat-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.stat-icon {
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

.stat-icon.salary-avg {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.salary-median {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.sample-count {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-large {
  height: 420px;
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

.map-controls {
  margin-top: 10px;
}

.chart-container {
  min-height: 400px;
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;

  .chart {
    flex: 1;
    width: 100%;
    min-height: 300px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}

.map-container {
  min-height: 600px;
  margin-bottom: 20px;

  .map-controls {
    margin-right: 20px;
  }
}
</style>
