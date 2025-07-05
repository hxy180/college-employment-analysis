<template>
  <div class="region-analysis-container">
    <el-row :gutter="20" class="mb-20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>地区分析筛选</span>
              <el-button type="success" @click="compareRegions">
                <el-icon><Rank /></el-icon>
                地区对比
              </el-button>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :span="6">
              <el-select v-model="selectedRegion" placeholder="选择地区" clearable @change="updateCharts">
                <el-option label="全部地区" value="" />
                <el-option
                  v-for="region in regions"
                  :key="region"
                  :label="region"
                  :value="region"
                />
              </el-select>
            </el-col>
            <el-col :span="6">
              <el-select v-model="sortBy" placeholder="排序方式" @change="updateCharts">
                <el-option label="按就业率" value="employmentRate" />
                <el-option label="按平均薪资" value="avgSalary" />
                <el-option label="按岗位数量" value="jobCount" />
                <el-option label="按地区名称" value="region" />
              </el-select>
            </el-col>
            <el-col :span="6">
              <el-select v-model="regionType" placeholder="地区类型" @change="updateCharts">
                <el-option label="全部类型" value="" />
                <el-option label="一线城市" value="tier1" />
                <el-option label="新一线城市" value="newTier1" />
                <el-option label="二线城市" value="tier2" />
              </el-select>
            </el-col>
            <el-col :span="6">
              <el-button type="primary" @click="exportRegionData">
                <el-icon><Download /></el-icon>
                导出数据
              </el-button>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mb-20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon region-count">
              <el-icon><Location /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ filteredRegions.length }}</div>
              <div class="stat-label">分析地区</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon avg-employment">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ averageEmploymentRate.toFixed(1) }}%</div>
              <div class="stat-label">平均就业率</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon top-region">
              <el-icon><Trophy /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ topEmploymentRegion?.region || '-' }}</div>
              <div class="stat-label">就业率最高</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon top-salary">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ topSalaryRegion?.region || '-' }}</div>
              <div class="stat-label">薪资最高</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区整体包裹，居中显示 -->
    <div class="charts-wrapper">
      <div class="charts-row">
        <div class="chart-container">
          <div class="chart-title">地区年就业率排名</div>
          <v-chart
            class="chart"
            :option="employmentRateRankingOption"
            :loading="loading"
            autoresize
          />
        </div>
        <div class="chart-container">
          <div class="chart-title">地区年平均薪资对比</div>
          <v-chart
            class="chart"
            :option="avgSalaryComparisonOption"
            :loading="loading"
            autoresize
          />
        </div>
      </div>
      <div class="charts-row">
        <div class="chart-container">
          <div class="chart-title">就业率与薪资关系散点图</div>
          <v-chart
            class="chart"
            :option="employmentSalaryScatterOption"
            :loading="loading"
            autoresize
          />
        </div>
        <div class="chart-container">
          <div class="chart-title">岗位数量分布</div>
          <v-chart
            class="chart"
            :option="jobCountDistributionOption"
            :loading="loading"
            autoresize
          />
        </div>
      </div>
      <div class="charts-row">
        <div class="chart-container">
          <div class="chart-title">地区综合评分雷达图</div>
          <v-chart
            class="chart"
            :option="regionRadarOption"
            :loading="loading"
            autoresize
          />
        </div>
        <div class="map-container">
          <div class="chart-title">地区详细数据表格</div>
          <el-input
            v-model="searchText"
            placeholder="搜索地区..."
            style="width: 200px"
            clearable
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-table :data="cityTableData" stripe>
            <el-table-column prop="regionFullName" label="地区" />
            <el-table-column prop="employment" label="年就业率">
              <template #default="{ row }">
                {{ row.employment }}%
              </template>
            </el-table-column>
            <el-table-column prop="avgSalary" label="年平均薪资">
              <template #default="{ row }">
                ¥{{ row.avgSalary.toLocaleString() }}
              </template>
            </el-table-column>
            <el-table-column prop="salaryGrowth" label="年薪资增速">
              <template #default="{ row }">
                {{ row.salaryGrowth }}%
              </template>
            </el-table-column>
            <el-table-column prop="graduateRatio" label="年毕业生占比">
              <template #default="{ row }">
                {{ row.graduateRatio }}%
              </template>
            </el-table-column>
            <el-table-column prop="industryTags" label="主要产业">
              <template #default="{ row }">
                <el-tag v-for="tag in row.industryTags" :key="tag" class="mr-5">{{ tag }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="城市等级">
              <template #default="{ row }">
                <el-tag :type="getCityTierType(row.region)">
                  {{ getCityTier(row.region) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="综合评分">
              <template #default="{ row }">
                <el-rate
                  :model-value="getComprehensiveScore(row)"
                  disabled
                  show-score
                  text-color="#ff9900"
                />
              </template>
            </el-table-column>
            <el-table-column label="操作">
              <template #default="{ row }">
                <el-button size="small" @click="viewRegionDetail(row)">
                  详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            :total="cityTableTotal"
            :current-page="cityTablePageNum"
            :page-size="cityTablePageSize"
            @current-change="handleCityTablePageChange"
            @size-change="handleCityTableSizeChange"
            layout="prev, pager, next"
          />
        </div>
      </div>
    </div>

    <!-- 地区详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="地区详情" width="60%">
      <div v-if="selectedRegionDetail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="地区名称">
            {{ selectedRegionDetail.region }}
          </el-descriptions-item>
          <el-descriptions-item label="年就业率">
            {{ selectedRegionDetail.employmentRate }}%
          </el-descriptions-item>
          <el-descriptions-item label="年平均薪资">
            ¥{{ selectedRegionDetail.avgSalary.toLocaleString()}}
          </el-descriptions-item>
          <el-descriptions-item label="年岗位数量">
            {{ selectedRegionDetail.jobCount.toLocaleString() }}
          </el-descriptions-item>
          <el-descriptions-item label="城市等级">
            <el-tag :type="getCityTierType(selectedRegionDetail.region)">
              {{ getCityTier(selectedRegionDetail.region) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="综合评分">
            <el-rate
              :model-value="getComprehensiveScore(selectedRegionDetail)"
              disabled
              show-score
            />
          </el-descriptions-item>
        </el-descriptions>

        <div class="mt-20">
          <h4>地区特色</h4>
          <p>{{ getRegionDescription(selectedRegionDetail.region) }}</p>
        </div>

        <div class="mt-20">
          <h4>主要产业</h4>
          <el-tag
            v-for="industry in getRegionIndustries(selectedRegionDetail.region)"
            :key="industry"
            class="mr-10 mb-10"
          >
            {{ industry }}
          </el-tag>
        </div>

        <div class="mt-20">
          <h4>就业优势</h4>
          <ul>
            <li v-for="advantage in getRegionAdvantages(selectedRegionDetail.region)" :key="advantage">
              {{ advantage }}
            </li>
          </ul>
        </div>
      </div>
    </el-dialog>

    <!-- 地区对比对话框 -->
    <el-dialog v-model="showCompareDialog" title="地区对比" width="80%">
      <el-row :gutter="20" class="mb-20">
        <el-col :span="12">
          <el-select v-model="compareRegion1" placeholder="选择地区1" style="width: 100%">
            <el-option
              v-for="region in regions"
              :key="region"
              :label="region"
              :value="region"
            />
          </el-select>
        </el-col>
        <el-col :span="12">
          <el-select v-model="compareRegion2" placeholder="选择地区2" style="width: 100%">
            <el-option
              v-for="region in regions"
              :key="region"
              :label="region"
              :value="region"
            />
          </el-select>
        </el-col>
      </el-row>

      <div v-if="compareRegion1 && compareRegion2">
        <v-chart
          class="chart-large"
          :option="regionCompareOption"
          autoresize
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { use } from 'echarts/core'
import { BarChart, PieChart, ScatterChart, RadarChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent, VisualMapComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

// 注册ECharts组件
use([BarChart, PieChart, ScatterChart, RadarChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent, VisualMapComponent, CanvasRenderer])
import { ElMessage } from 'element-plus'

const loading = ref(false)
const selectedRegion = ref('')
const sortBy = ref('employmentRate')
const regionType = ref('')
const searchText = ref('')
const showDetailDialog = ref(false)
const showCompareDialog = ref(false)
const selectedRegionDetail = ref<any>(null)
const compareRegion1 = ref('')
const compareRegion2 = ref('')

// 新增：地区数据本地变量
const regionData = ref<any[]>([])

// 新增：城市表格数据本地变量和分页
const cityTableData = ref<any[]>([])
const cityTableTotal = ref(0)
const cityTablePageNum = ref(1)
const cityTablePageSize = ref(10)

// 页面加载时从接口获取数据
onMounted(() => {
  loading.value = true
  fetch('http://localhost:9908/api/summaryRegion/mapData')
    .then(res => res.json())
    .then(data => {
      if (data.code === 200) {
        // 适配字段：name->region, value->jobCount, salary->avgSalary, employmentRate 随机或用默认
        regionData.value = data.data.map((item: any) => ({
          region: item.name,
          jobCount: item.value,
          avgSalary: Math.round(item.salary),
          employmentRate: Math.round((80 + Math.random() * 20) * 10) / 10 // 80~100 随机模拟
        }))
      }
    })
    .finally(() => {
      loading.value = false
    })
  fetchCityTableData()
})

// 计算属性
const regions = computed(() =>
  regionData.value.map(item => item.region)
)

const filteredRegions = computed(() => {
  let data = [...regionData.value]

  if (selectedRegion.value) {
    data = data.filter(item => item.region === selectedRegion.value)
  }

  if (regionType.value) {
    data = data.filter(item => {
      const tier = getCityTier(item.region)
      return (
        (regionType.value === 'tier1' && tier === '一线城市') ||
        (regionType.value === 'newTier1' && tier === '新一线城市') ||
        (regionType.value === 'tier2' && tier === '二线城市')
      )
    })
  }

  // 排序
  if (sortBy.value === 'employmentRate') {
    data.sort((a, b) => b.employmentRate - a.employmentRate)
  } else if (sortBy.value === 'avgSalary') {
    data.sort((a, b) => b.avgSalary - a.avgSalary)
  } else if (sortBy.value === 'jobCount') {
    data.sort((a, b) => b.jobCount - a.jobCount)
  } else if (sortBy.value === 'region') {
    data.sort((a, b) => a.region.localeCompare(b.region))
  }

  return data
})

const averageEmploymentRate = computed(() => {
  const sum = filteredRegions.value.reduce((acc, item) => acc + item.employmentRate, 0)
  return filteredRegions.value.length > 0 ? sum / filteredRegions.value.length : 0
})

const topEmploymentRegion = computed(() =>
  filteredRegions.value.length > 0
    ? filteredRegions.value.reduce((max, item) =>
        item.employmentRate > max.employmentRate ? item : max,
      filteredRegions.value[0] as { region: string; employmentRate: number; avgSalary: number; jobCount: number })
    : null
)

const topSalaryRegion = computed(() =>
  filteredRegions.value.length > 0
    ? filteredRegions.value.reduce((max, item) =>
        item.avgSalary > max.avgSalary ? item : max,
      filteredRegions.value[0] as { region: string; employmentRate: number; avgSalary: number; jobCount: number })
    : null
)

const filteredTableData = computed(() => {
  let data = filteredRegions.value
  if (searchText.value) {
    data = data.filter(item =>
      item.region.toLowerCase().includes(searchText.value.toLowerCase())
    )
  }
  return data
})

// 就业率排名图配置
const employmentRateRankingOption = computed(() => ({
  title: {
    text: '地区年就业率排名',
    left: 'center'
  },
  tooltip: {
    trigger: 'axis',
    formatter: (params: any) => {
      const data = params[0]
      return `${data.name}<br/>就业率: ${data.value}%`
    }
  },
  xAxis: {
    type: 'category',
    data: filteredRegions.value.slice(0, 10).map(item => item.region),
    axisLabel: {
      rotate: 45
    }
  },
  yAxis: {
    type: 'value',
    min: 80,
    axisLabel: {
      formatter: '{value}%'
    }
  },
  series: [{
    data: filteredRegions.value.slice(0, 10).map(item => item.employmentRate),
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
}))

// 平均薪资对比图配置
const avgSalaryComparisonOption = computed(() => ({
  title: {
    text: '地区年平均薪资对比',
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
    data: filteredRegions.value
      .sort((a, b) => b.avgSalary - a.avgSalary)
      .slice(0, 10)
      .map(item => item.region),
    axisLabel: {
      rotate: 45
    }
  },
  yAxis: {
    type: 'value',
    axisLabel: {
      formatter: '¥{value}'
    }
  },
  series: [{
    data: filteredRegions.value
      .sort((a, b) => b.avgSalary - a.avgSalary)
      .slice(0, 10)
      .map(item => item.avgSalary),
    type: 'bar',
    itemStyle: {
      color: {
        type: 'linear',
        x: 0,
        y: 0,
        x2: 0,
        y2: 1,
        colorStops: [{
          offset: 0, color: '#67C23A'
        }, {
          offset: 1, color: '#95D475'
        }]
      }
    }
  }]
}))

// 就业率与薪资散点图配置
const employmentSalaryScatterOption = computed(() => {
  // 先拿到有效数据
  const regionsData = filteredRegions.value;
  // 计算 x 轴（就业率）的最小、最大值，留一定缓冲
  const xMin = Math.min(...regionsData.map(item => item.employmentRate)) - 2;
  const xMax = Math.max(...regionsData.map(item => item.employmentRate)) + 2;
  // y 轴（薪资）同理，根据数据动态算
  const yMin = Math.min(...regionsData.map(item => item.avgSalary)) - 5000;
  const yMax = Math.max(...regionsData.map(item => item.avgSalary)) + 5000;

  return {
    title: {
      text: '地区年就业率与年平均薪资关系',
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
        const region = regionsData[params.dataIndex]
        return `${region.region}<br/>就业率: ${region.employmentRate}%<br/>平均薪资: ¥${region.avgSalary.toLocaleString()}<br/>岗位数量: ${region.jobCount.toLocaleString()}`
      }
    },
    xAxis: {
      type: 'value',
      name: '就业率 (%)',
      nameLocation: 'middle',
      nameGap: 30,
      min: xMin, // 动态最小值
      max: xMax  // 动态最大值
    },
    yAxis: {
      type: 'value',
      name: '平均薪资 (元)',
      nameLocation: 'middle',
      nameGap: 50,
      axisLabel: {
        formatter: '¥{value}'
      },
      min: yMin, // 动态最小值
      max: yMax  // 动态最大值
    },
    series: [{
      symbolSize: (data: any) => Math.sqrt(data[2]) / 20,
      data: regionsData.map(item => [item.employmentRate, item.avgSalary, item.jobCount]),
      type: 'scatter',
      itemStyle: {
        color: '#409EFF',
        opacity: 0.7
      }
    }]
  }
})

// 岗位数量分布图配置
const jobCountDistributionOption = computed(() => ({
  title: {
    text: '地区年岗位数量分布',
    left: 'center'
  },
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b}: {c} ({d}%)'
  },
  series: [{
    name: '岗位年数量',
    type: 'pie',
    radius: '70%',
    data: filteredRegions.value.slice(0, 8).map(item => ({
      name: item.region,
      value: item.jobCount
    })),
    emphasis: {
      itemStyle: {
        shadowBlur: 10,
        shadowOffsetX: 0,
        shadowColor: 'rgba(0, 0, 0, 0.5)'
      }
    }
  }]
}))

// 地区雷达图配置
const regionRadarOption = computed(() => {
  const topRegions = filteredRegions.value.slice(0, 5)
  const maxEmploymentRate = Math.max(...topRegions.map(r => r.employmentRate))
  const maxSalary = Math.max(...topRegions.map(r => r.avgSalary))
  const maxJobCount = Math.max(...topRegions.map(r => r.jobCount))

  return {
    title: {
      text: '地区综合评分对比',
      left: 'center'
    },
    tooltip: {},
    legend: {
      data: topRegions.map(r => r.region),
      bottom: 0
    },
    radar: {
      indicator: [
        { name: '就业率', max: 100 },
        { name: '年平均薪资', max: 200000 },
        { name: '岗位数量', max: maxJobCount },
        { name: '发展潜力', max: 100 },
        { name: '生活成本', max: 100 }
      ],
      center: ['50%', '50%'],
      radius: '60%'
    },
    series: topRegions.map(region => ({
      name: region.region,
      type: 'radar',
      data: [{
        value: [
          region.employmentRate,
          region.avgSalary,
          region.jobCount,
          Math.random() * 100, // 模拟发展潜力
          Math.random() * 100  // 模拟生活成本
        ],
        name: region.region
      }]
    }))
  }
})

const regionCompareOption = computed(() => {
  if (!compareRegion1.value || !compareRegion2.value) return {}

  const region1Data = regionData.value.find(r => r.region === compareRegion1.value)
  const region2Data = regionData.value.find(r => r.region === compareRegion2.value)

  if (!region1Data || !region2Data) return {}

  // 合并两个地区的数据，用于计算各维度的最大值
  const combinedData = [region1Data, region2Data];

  // 动态计算各维度的最大值（增加10%余量）
  const employmentRateMax = Math.max(...combinedData.map(r => r.employmentRate)) * 1.1;
  const avgSalaryMax = Math.max(...combinedData.map(r => r.avgSalary)) * 1.1;
  const jobCountMax = Math.max(...combinedData.map(r => r.jobCount)) * 1.1;
  // 发展潜力和生活质量默认最大100（如果随机值可能超过100，也可以动态计算）
  const potentialMax = 100;
  const lifeQualityMax = 100;

  return {
    title: {
      text: `${compareRegion1.value} vs ${compareRegion2.value}`,
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: [compareRegion1.value, compareRegion2.value],
      bottom: 0
    },
    radar: {
      indicator: [
        { name: '就业率', max: employmentRateMax },
        { name: '年平均薪资', max: avgSalaryMax },
        { name: '岗位数量', max: jobCountMax },
        { name: '发展潜力', max: potentialMax },
        { name: '生活质量', max: lifeQualityMax }
      ],
      center: ['50%', '50%'],
      radius: '60%'
    },
    series: [{
      name: '地区对比',
      type: 'radar',
      data: [
        {
          value: [
            region1Data.employmentRate,
            region1Data.avgSalary,
            region1Data.jobCount,
            // 建议使用固定值而非随机数，保持数据一致性
            region1Data.developmentPotential || Math.random() * 100,
            region1Data.qualityOfLife || Math.random() * 100
          ],
          name: compareRegion1.value,
          areaStyle: {
            color: 'rgba(64, 158, 255, 0.3)'
          }
        },
        {
          value: [
            region2Data.employmentRate,
            region2Data.avgSalary,
            region2Data.jobCount,
            // 建议使用固定值而非随机数，保持数据一致性
            region2Data.developmentPotential || Math.random() * 100,
            region2Data.qualityOfLife || Math.random() * 100
          ],
          name: compareRegion2.value,
          areaStyle: {
            color: 'rgba(103, 194, 58, 0.3)'
          }
        }
      ]
    }]
  }
})

// 获取进度条颜色
const getProgressColor = (rate: number) => {
  if (rate >= 90) return '#67C23A'
  if (rate >= 85) return '#E6A23C'
  return '#F56C6C'
}

// 获取城市等级
const getCityTier = (region: string) => {
  const tier1Cities = ['北京', '上海', '深圳', '广州']
  const newTier1Cities = ['杭州', '南京', '成都', '武汉', '西安', '重庆']

  if (tier1Cities.includes(region)) return '一线城市'
  if (newTier1Cities.includes(region)) return '新一线城市'
  return '二线城市'
}

// 获取城市等级类型
const getCityTierType = (region: string) => {
  const tier = getCityTier(region)
  if (tier === '一线城市') return 'danger'
  if (tier === '新一线城市') return 'warning'
  return 'success'
}

// 获取综合评分
const getComprehensiveScore = (region: any) => {
  const employmentScore = region.employmentRate / 20
  const salaryScore = region.avgSalary / 3000
  const jobScore = region.jobCount / 200000
  const totalScore = (employmentScore + salaryScore + jobScore) / 3
  return Math.min(Math.round(totalScore), 5)
}

// 查看地区详情
const viewRegionDetail = (region: any) => {
  selectedRegionDetail.value = region
  showDetailDialog.value = true
}

// 地区对比
const compareRegions = () => {
  showCompareDialog.value = true
}

// 获取地区描述
const getRegionDescription = (regionName: string) => {
  const descriptions: Record<string, string> = {
    '北京': '首都北京是全国的政治、文化、国际交往、科技创新中心，拥有丰富的就业机会和发展平台。',
    '上海': '国际经济、金融、贸易、航运中心，是中国最具国际化程度的城市之一。',
    '深圳': '中国改革开放的窗口，科技创新之城，拥有众多高新技术企业和创业机会。',
    '广州': '华南地区的经济、政治、文化中心，商贸发达，就业机会丰富。',
    '杭州': '电子商务之都，互联网企业聚集，创新创业氛围浓厚。'
  }
  return descriptions[regionName] || '该地区经济发展良好，为毕业生提供了多样化的就业选择。'
}

// 获取地区主要产业
const getRegionIndustries = (regionName: string) => {
  const industries: Record<string, string[]> = {
    '北京': ['互联网', '金融', '教育', '文化创意', '高新技术'],
    '上海': ['金融', '贸易', '制造业', '航运物流', '生物医药'],
    '深圳': ['电子信息', '生物医药', '新能源', '新材料', '文化创意'],
    '广州': ['汽车制造', '石化', '电子信息', '生物医药', '现代服务业'],
    '杭州': ['电子商务', '信息软件', '文化创意', '金融服务', '旅游休闲']
  }
  return industries[regionName] || ['制造业', '服务业', '高新技术', '现代农业', '文化产业']
}

// 获取地区就业优势
const getRegionAdvantages = (regionName: string) => {
  const advantages: Record<string, string[]> = {
    '北京': ['政策资源丰富', '教育资源优质', '科研院所众多', '国际化程度高', '创业环境良好'],
    '上海': ['国际化程度高', '金融业发达', '产业体系完整', '人才政策优惠', '生活便利度高'],
    '深圳': ['创新氛围浓厚', '年轻化程度高', '创业机会多', '薪资水平高', '发展速度快'],
    '广州': ['生活成本适中', '文化包容性强', '交通便利', '产业基础雄厚', '就业机会多样'],
    '杭州': ['互联网产业发达', '生活环境优美', '创业成本较低', '人才政策优惠', '发展潜力大']
  }
  return advantages[regionName] || ['经济发展稳定', '就业机会较多', '生活成本适中', '发展前景良好', '人才政策支持']
}

// 更新图表
const updateCharts = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 500)
}

// 导出地区数据
const exportRegionData = () => {
  ElMessage.success('地区数据导出功能开发中...')
}

// 获取城市表格数据方法
const fetchCityTableData = () => {
  fetch(`http://localhost:9908/api/summaryRegion/cityTable?pageNum=${cityTablePageNum.value}&pageSize=${cityTablePageSize.value}`)
    .then(res => res.json())
    .then(data => {
      if (data.code === 200) {
        cityTableData.value = data.data.list
        cityTableTotal.value = data.data.total
      }
    })
}

// 监听分页变化
const handleCityTablePageChange = (page: number) => {
  cityTablePageNum.value = page
  fetchCityTableData()
}

const handleCityTableSizeChange = (size: number) => {
  cityTablePageSize.value = size
  cityTablePageNum.value = 1
  fetchCityTableData()
}
</script>

<style scoped>
:root, html, body, #app {
  width: 100vw;
  min-height: 100vh;
  margin: 0;
  padding: 0;
}
.main-container, .region-analysis-wrapper, .region-analysis-content {
  width: 100vw !important;
  min-height: 100vh !important;
  margin: 0 !important;
  padding: 0 !important;
  box-sizing: border-box;
}
.region-analysis-container {
  background: transparent;
  padding: 20px 40px;
  min-height: 100vh;
  box-sizing: border-box;
}

.filter-row {
  display: flex;
  gap: 20px;
  align-items: center;
}

.chart-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
  margin-bottom: 24px;
}

.el-card, .chart-container, .map-container, .data-table {
  background: #fff !important;
  border-radius: 12px !important;
  box-shadow: 0 4px 24px rgba(0,0,0,0.06) !important;
}

.chart-container {
  padding: 20px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  height: 400px;
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

.map-container {
  flex: 2;
  padding: 20px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.data-table {
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
  margin-bottom: 24px !important;
}

.mt-20 {
  margin-top: 20px;
}

.mr-10 {
  margin-right: 10px;
}

.mb-10 {
  margin-bottom: 10px;
}

.stat-card {
  height: 120px;
  border-radius: 12px !important;
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

.stat-icon.region-count {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.avg-employment {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.top-region {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.top-salary {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
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
  height: 400px;
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

:deep(.el-progress) {
  margin-right: 10px;
}

:deep(.el-rate) {
  display: flex;
  align-items: center;
}

.el-row {
  margin-left: 0 !important;
  margin-right: 0 !important;
}

.el-col {
  padding-left: 0 !important;
  padding-right: 0 !important;
}

.charts-wrapper {
  width: 100%;
  margin: 0;
  padding: 0;
}
.charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 24px;
}
.chart-container, .map-container {
  height: 400px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.06);
  padding: 20px;
  display: flex;
  flex-direction: column;
}
</style>
