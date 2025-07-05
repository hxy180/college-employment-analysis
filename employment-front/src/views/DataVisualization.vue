<template>
  <div class="data-visualization-screen">
    <div class="content-wrapper">
      <!-- 顶部标题栏 -->
      <div class="screen-header">
        <div class="header-left">
          <div class="current-time">{{ currentTime }}</div>
        </div>
        <div class="header-center">
          <h1 class="main-title">大学生就业大数据可视化平台</h1>
          <div class="subtitle">University Graduate Employment Big Data Visualization Platform</div>
        </div>
        <div class="header-right">
          <div class="update-info">数据更新时间: {{ updateTime }}</div>
        </div>
      </div>

      <!-- 主要内容区域 -->
      <div class="screen-content grid-layout">
        <!-- 左侧面板 -->
        <div class="left-panel">
          <!-- 用户分布（大环形图） -->
          <div class="panel-section user-distribution-section">
            <div class="section-title">
              <span>用户分布</span>
            </div>
            <v-chart class="user-pie-chart" :option="userDistributionOption" />
          </div>
          <!-- 学历结构分布（环形图） -->
          <div class="panel-section degree-structure-section">
            <div class="section-title">
              <span>毕业生学历结构分布</span>
            </div>
            <v-chart class="degree-structure-chart" :option="degreeStructureOption" />
          </div>
          <!-- 就业去向分布（饼图） -->
          <div class="panel-section data-destination-section">
            <div class="section-title">
              <span>就业去向分布</span>
            </div>
            <v-chart class="data-destination-chart" :option="employmentDestinationOption" />
          </div>
        </div>

        <!-- 中央地图区域 -->
        <div class="center-panel">
          <div class="map-container">
            <div class="section-title">
              <span>全国就业分布图</span>
            </div>
            <div class="map-wrapper">
              <v-chart v-if="chinaMapReady" class="map-chart" :option="chinaMapOption" autoresize />
              <div v-else style="color:#00a1ff;text-align:center;padding:40px;">地图加载中...</div>
            </div>
          </div>
          <!-- 底部趋势图 -->
          <div class="trend-container">
            <div class="section-title">
              <span>就业趋势分析</span>
            </div>
            <v-chart class="trend-chart" :option="trendOption" autoresize />
          </div>
        </div>

        <!-- 右侧面板 -->
        <div class="right-panel">
          <!-- 数据统计（折线/面积图） -->
          <div class="panel-section salary-section">
            <div class="section-title">
              <span>数据统计</span>
            </div>
            <v-chart class="salary-chart" :option="salaryAnalysisOption" />
          </div>
          <!-- 行业排行（条形图） -->
          <div class="panel-section industry-section">
            <div class="section-title">
              <span>热门行业</span>
            </div>
            <v-chart class="industry-bar-chart" :option="industryBarOption" />
          </div>
          <!-- 就业关键词云 -->
          <div class="panel-section wordcloud-section">
            <div class="section-title">
              <span>就业关键词云</span>
            </div>
            <v-chart class="wordcloud-chart" :option="wordCloudOption" />
          </div>
        </div>
      </div>

      <!-- 底部状态栏 -->
      <div class="screen-footer">
        <div class="footer-left">
          <span class="status-indicator online"></span>
          <span>系统运行正常</span>
        </div>
        <div class="footer-center">
          <span>数据来源: 教育部、人社部、各大招聘平台</span>
        </div>
        <div class="footer-right">
          <span>总人数: {{ onlineUsers }}</span>
        </div>
      </div>
    </div>

    <!-- 装饰性粒子效果 -->
    <div class="particle-container">
      <div class="particle" v-for="n in 20" :key="n" :style="getParticleStyle()"></div>
    </div>

    <!-- 浮动导航控制面板 -->
    <div class="floating-nav" :class="{ 'nav-expanded': isNavExpanded }">
      <div class="nav-toggle" @click="toggleNav">
        <el-icon class="nav-icon">
          <Menu v-if="!isNavExpanded" />
          <Close v-else />
        </el-icon>
      </div>

      <div class="nav-menu" v-show="isNavExpanded">
        <div class="nav-title">功能导航</div>
        <div class="nav-grid">
          <div class="nav-item" @click="navigateTo('/depth-analysis/employment-overview')">
            <el-icon><DataLine /></el-icon>
            <span>就业概况</span>
          </div>
          <div class="nav-item" @click="navigateTo('/depth-analysis/major-analysis')">
            <el-icon><Grid /></el-icon>
            <span>专业分析</span>
          </div>
          <div class="nav-item" @click="navigateTo('/depth-analysis/region-analysis')">
            <el-icon><Location /></el-icon>
            <span>地区分析</span>
          </div>
          <div class="nav-item" @click="navigateTo('/depth-analysis/salary-analysis')">
            <el-icon><Money /></el-icon>
            <span>薪资分析</span>
          </div>
          <div class="nav-item" @click="navigateTo('/depth-analysis/trend-analysis')">
            <el-icon><TrendCharts /></el-icon>
            <span>趋势分析</span>
          </div>
          <div class="nav-item" @click="navigateTo('/depth-analysis/industry-analysis')">
            <el-icon><DataLine /></el-icon>
            <span>行业分析</span>
          </div>
          <div class="nav-item" @click="navigateTo('/reports/employment')">
            <el-icon><Document /></el-icon>
            <span>报告中心</span>
          </div>
          <div class="nav-item" @click="navigateTo('/help')">
            <el-icon><QuestionFilled /></el-icon>
            <span>帮助中心</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="DataVisualizationScreen">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import VChart from 'vue-echarts'
import * as echarts from 'echarts'
import type { CallbackDataParams } from 'echarts/types/dist/shared'
import {
  Menu,
  Close,
  Money,
  TrendCharts,
  Location,
  Document,
  DataLine,
  QuestionFilled,
  Grid,
} from '@element-plus/icons-vue'
import { useIntervalFn } from '@vueuse/core'

// 导入必要的 ECharts 组件
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { MapChart } from 'echarts/charts'
import 'echarts-wordcloud'
import {
  TitleComponent,
  TooltipComponent,
  VisualMapComponent,
  GeoComponent,
  GridComponent,
  LegendComponent
} from 'echarts/components'

// 注册组件
use([
  CanvasRenderer,
  MapChart,
  TitleComponent,
  TooltipComponent,
  VisualMapComponent,
  GeoComponent,
  GridComponent,
  LegendComponent
])

const router = useRouter()

interface MapDataItem {
  name: string;
  employment: number;
  salary: number;
  count: number;
}

const mapData = ref<any[]>([])

// 定义变量
const currentTime = ref('')
const updateTime = ref('2025-01-15 14:30:00')
const onlineUsers = ref('8000')
const chinaMapReady = ref(false)

// 地图配置
const chinaMapOption = computed(() => ({
  backgroundColor: 'transparent',
  tooltip: {
    trigger: 'item',
    formatter: (params: any) => {
      if (params.value) {
        return `${params.name}<br/>就业人数：${params.value}人`;
      }
      return params.name;
    }
  },
  visualMap: {
    type: 'continuous',
    left: 'left',
    bottom: '10%',
    dimension: 0,
    text: ['高', '低'],
    itemWidth: 12,
    itemHeight: 120,
    inRange: {
      color: ['#073684', '#00a1ff', '#73d13d']
    },
    textStyle: {
      color: '#fff'
    },
    min: 10000,
    max: 150000
  },
  geo: {
    map: 'china',
    roam: true,
    scaleLimit: {
      min: 1,
      max: 5
    },
    zoom: 1.2,
    label: {
      show: true,
      fontSize: 10,
      color: '#fff'
    },
    itemStyle: {
      areaColor: '#073684',
      borderColor: '#00a1ff',
      borderWidth: 1
    },
    emphasis: {
      label: {
        show: true,
        fontSize: 12,
        color: '#fff'
      },
      itemStyle: {
        areaColor: '#00a1ff',
        shadowColor: 'rgba(0, 0, 0, 0.5)',
        shadowBlur: 10
      }
    }
  },
  series: [
    {
      name: '就业人数',
      type: 'map',
      geoIndex: 0,
      data: mapData.value
    }
  ]
}))

// 导航控制
const isNavExpanded = ref(false)
const toggleNav = () => {
  isNavExpanded.value = !isNavExpanded.value
}

// 导航方法
const navigateTo = (path: string) => {
  router.push(path)
}

// 粒子效果
const getParticleStyle = () => {
  const size = Math.random() * 4 + 1
  const top = Math.random() * 100
  const left = Math.random() * 100
  const animationDuration = Math.random() * 20 + 10
  const animationDelay = Math.random() * 5

  return {
    width: `${size}px`,
    height: `${size}px`,
    top: `${top}%`,
    left: `${left}%`,
    animationDuration: `${animationDuration}s`,
    animationDelay: `${animationDelay}s`
  }
}

// 关键指标数据
const keyMetrics = ref([
  { id: 1, value: '90.5%', label: '就业率', change: '+1.4%', trend: 'up' },
  { id: 2, value: '1179', label: '毕业生', change: '+2.3%', trend: 'up' },
  { id: 3, value: '11.2%', label: '薪资增长', change: '+0.8%', trend: 'up' },
  { id: 4, value: '8657', label: '就业岗位', change: '+126', trend: 'up' }
])

// 实时数据流
const dataStream = ref([
  { id: 1, time: '10:04:06', content: '深圳新增就业企业', value: '+98.8', type: 'up' },
  { id: 2, time: '10:03:52', content: '北京区域新就业岗位', value: '+126', type: 'up' },
  { id: 3, time: '10:03:42', content: '上海行业薪资上涨', value: '+3.2%', type: 'up' },
  { id: 4, time: '10:03:18', content: '深圳新增招聘企业', value: '+45', type: 'up' },
  { id: 5, time: '10:02:56', content: '广州就业率提升', value: '+0.8%', type: 'up' }
])

// 行业排名数据
const industryRanking = ref([
  { name: 'IT/互联网', percentage: 95 },
  { name: '金融', percentage: 85 },
  { name: '教育', percentage: 75 },
  { name: '医疗健康', percentage: 65 },
  { name: '制造业', percentage: 55 }
])

// 专业分布饼图配置
const majorDistributionOption = computed(() => ({
  color: ['#00a1ff', '#52c41a', '#fa8c16', '#eb2f96', '#722ed1'],
  tooltip: {
    trigger: 'item'
  },
  series: [
    {
      name: '专业分布',
      type: 'pie',
      radius: ['50%', '70%'],
      center: ['50%', '50%'],
      data: [
        { value: 35, name: '计算机' },
        { value: 25, name: '金融' },
        { value: 20, name: '管理' },
        { value: 15, name: '工程' },
        { value: 5, name: '其他' }
      ],
      label: {
        show: true,
        position: 'outside',
        formatter: '{b}\n{d}%',
        color: '#fff'
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
}))

// 趋势图配置
const trendOption = computed(() => ({
  color: ['#00a1ff', '#ff6b9d', '#52c41a'],
  tooltip: {
    trigger: 'axis'
  },
  legend: {
    data: ['就业率', '平均薪资', '就业人数'],
    textStyle: { color: '#fff' }
  },
  grid: {
    top: 30,
    right: 30,
    bottom: 30,
    left: 50,
    containLabel: true
  },
  xAxis: {
    type: 'category',
    data: ['2019', '2020', '2021', '2022', '2023', '2024'],
    axisLine: {
      lineStyle: {
        color: '#00a1ff'
      }
    },
    axisLabel: {
      color: '#fff'
    }
  },
  yAxis: [
    {
      type: 'value',
      name: '就业率(%)',
      min: 0,
      max: 100,
      position: 'left',
      axisLine: { lineStyle: { color: '#00a1ff' } },
      axisLabel: { color: '#fff' },
      splitLine: { lineStyle: { color: 'rgba(0, 161, 255, 0.1)' } }
    },
    {
      type: 'value',
      name: '平均薪资(元/月)',
      min: 6000,
      max: 16000,
      position: 'right',
      axisLine: { lineStyle: { color: '#ff6b9d' } },
      axisLabel: { color: '#fff' },
      splitLine: { show: false }
    },
    {
      type: 'value',
      name: '就业人数',
      min: 10000,
      max: 30000,
      position: 'right',
      offset: 60,
      axisLine: { lineStyle: { color: '#52c41a' } },
      axisLabel: { color: '#fff' },
      splitLine: { show: false }
    }
  ],
  series: [
    {
      name: '就业率',
      type: 'line',
      smooth: true,
      yAxisIndex: 0,
      data: [82, 80, 85, 88, 90, 92],
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: { width: 3 },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(0, 161, 255, 0.3)' },
            { offset: 1, color: 'rgba(0, 161, 255, 0.1)' }
          ]
        }
      }
    },
    {
      name: '平均薪资',
      type: 'line',
      smooth: true,
      yAxisIndex: 1,
      data: [9000, 9500, 10500, 11500, 13000, 14500],
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: { width: 3 }
    },
    {
      name: '就业人数',
      type: 'line',
      smooth: true,
      yAxisIndex: 2,
      data: [12000, 15000, 18000, 21000, 25000, 28000],
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: { width: 3 }
    }
  ]
}))

// 薪资分析配置
const salaryAnalysisOption = computed(() => ({
  color: ['#00a1ff', '#52c41a', '#fa8c16', '#eb2f96', '#722ed1'],
  tooltip: { trigger: 'axis' },
  legend: {
    data: ['计算机', '金融', '医疗', '教育', '制造'],
    textStyle: { color: '#fff' }
  },
  grid: {
    top: 30,
    right: 30,
    bottom: 30,
    left: 50,
    containLabel: true
  },
  xAxis: {
    type: 'category',
    data: ['2019', '2020', '2021', '2022', '2023', '2024'],
    axisLine: { lineStyle: { color: '#00a1ff' } },
    axisLabel: { color: '#fff', rotate: 0 }
  },
  yAxis: {
    type: 'value',
    axisLine: { lineStyle: { color: '#00a1ff' } },
    axisLabel: { color: '#fff' },
    splitLine: { lineStyle: { color: 'rgba(0, 161, 255, 0.1)' } }
  },
  series: [
    {
      name: '计算机',
      type: 'line',
      stack: '总量',
      areaStyle: {
        color: {
          type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(0,161,255,0.5)' },
            { offset: 1, color: 'rgba(0,161,255,0.1)' }
          ]
        }
      },
      emphasis: { focus: 'series' },
      data: [12000, 13000, 13500, 14000, 14500, 15000]
    },
    {
      name: '金融',
      type: 'line',
      stack: '总量',
      areaStyle: {
        color: {
          type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(82,196,26,0.5)' },
            { offset: 1, color: 'rgba(82,196,26,0.1)' }
          ]
        }
      },
      emphasis: { focus: 'series' },
      data: [9000, 10000, 11000, 11500, 12000, 12500]
    },
    {
      name: '医疗',
      type: 'line',
      stack: '总量',
      areaStyle: {
        color: {
          type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(250,140,22,0.5)' },
            { offset: 1, color: 'rgba(250,140,22,0.1)' }
          ]
        }
      },
      emphasis: { focus: 'series' },
      data: [8000, 9000, 9500, 10000, 10500, 11000]
    },
    {
      name: '教育',
      type: 'line',
      stack: '总量',
      areaStyle: {
        color: {
          type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(235,47,150,0.5)' },
            { offset: 1, color: 'rgba(235,47,150,0.1)' }
          ]
        }
      },
      emphasis: { focus: 'series' },
      data: [7000, 8000, 8500, 9000, 9500, 10000]
    },
    {
      name: '制造',
      type: 'line',
      stack: '总量',
      areaStyle: {
        color: {
          type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(114,46,209,0.5)' },
            { offset: 1, color: 'rgba(114,46,209,0.1)' }
          ]
        }
      },
      emphasis: { focus: 'series' },
      data: [6000, 7000, 7500, 8000, 8500, 9000]
    }
  ]
}))

// 用户分布极坐标柱状图配置
const userDistributionOption = computed(() => ({
  angleAxis: {
    type: 'category',
    data: ['华东', '华南', '华北', '西部'],
    axisLine: { lineStyle: { color: '#00cfff' } },
    axisLabel: { color: '#fff' }
  },
  radiusAxis: {
    axisLine: { lineStyle: { color: '#00cfff' } },
    axisLabel: { color: '#fff' },
    splitLine: { lineStyle: { color: 'rgba(0,207,255,0.1)' } }
  },
  polar: {},
  tooltip: { trigger: 'item' },
  series: [
    {
      type: 'bar',
      data: [
        { value: 218, itemStyle: { color: '#00cfff' } },
        { value: 156, itemStyle: { color: '#52c41a' } },
        { value: 98, itemStyle: { color: '#fa8c16' } },
        { value: 76, itemStyle: { color: '#eb2f96' } }
      ],
      coordinateSystem: 'polar',
      barWidth: 24,
      itemStyle: {
        borderRadius: 12,
        shadowBlur: 10,
        shadowColor: 'rgba(0, 207, 255, 0.5)'
      },
      label: {
        show: true,
        position: 'outside',
        color: '#fff',
        formatter: params => `${params.name}\n${params.value}`
      }
    }
  ],
  legend: { show: false }
}))

// 就业关键词云数据
const wordCloudData = ref([
  { name: 'IT/互联网', value: 120 },
  { name: '金融', value: 95 },
  { name: '人工智能', value: 85 },
  { name: '大数据', value: 80 },
  { name: '软件开发', value: 75 },
  { name: '市场营销', value: 70 },
  { name: '教育', value: 65 },
  { name: '医疗健康', value: 60 },
  { name: '制造业', value: 55 },
  { name: '电子商务', value: 50 },
  { name: '数据分析', value: 48 },
  { name: '产品经理', value: 45 },
  { name: '云计算', value: 42 },
  { name: '网络安全', value: 40 },
  { name: 'UI/UX设计', value: 38 },
  { name: '运营管理', value: 35 },
  { name: '人力资源', value: 32 },
  { name: '财务', value: 30 },
  { name: '法律', value: 25 },
  { name: '物流', value: 20 }
])

// 词云配置
const wordCloudOption = computed(() => ({
  backgroundColor: 'transparent',
  tooltip: { trigger: 'item' },
  series: [{
    type: 'wordCloud',
    sizeRange: [12, 50],
    rotationRange: [-90, 90],
    rotationStep: 45,
    gridSize: 8,
    drawOutOfBound: false,
    layoutAnimation: true,
    textStyle: {
      color: function() {
        return 'rgb(' +
          Math.round(Math.random() * 100) + ', ' +
          Math.round(Math.random() * 200) + ', ' +
          Math.round(Math.random() * 255) + ')';
      },
      emphasis: {
        shadowBlur: 10,
        shadowColor: '#00a1ff'
      }
    },
    data: wordCloudData.value
  }]
}))

// 行业排行条形图
const industryBarOption = computed(() => ({
  color: ['#00cfff', '#52c41a', '#fa8c16', '#eb2f96', '#722ed1'],
  tooltip: { trigger: 'axis' },
  grid: { left: 60, right: 30, top: 30, bottom: 30 },
  xAxis: {
    type: 'value',
    axisLine: { lineStyle: { color: '#00a1ff' } },
    axisLabel: { color: '#fff' },
    splitLine: { lineStyle: { color: 'rgba(0, 161, 255, 0.1)' } }
  },
  yAxis: {
    type: 'category',
    data: ['IT/互联网', '金融', '教育', '医疗健康', '制造业'],
    axisLine: { lineStyle: { color: '#00a1ff' } },
    axisLabel: { color: '#fff' }
  },
  series: [{
    type: 'bar',
    data: [
      { value: 95, itemStyle: { color: '#00cfff' } },
      { value: 85, itemStyle: { color: '#52c41a' } },
      { value: 75, itemStyle: { color: '#fa8c16' } },
      { value: 65, itemStyle: { color: '#eb2f96' } },
      { value: 55, itemStyle: { color: '#722ed1' } }
    ],
    barWidth: 18,
    itemStyle: { borderRadius: [8, 8, 8, 8] },
    label: { show: true, position: 'right', color: '#fff' }
  }]
}))

// 数据播报（滚动列表）
const newsList = ref([
  { id: 1, time: '10:44:01', content: '全国就业形势持续向好' },
  { id: 2, time: '10:43:58', content: 'IT行业就业率创新高' },
  { id: 3, time: '10:43:55', content: '金融行业薪资稳步提升' },
  { id: 4, time: '10:43:52', content: '教育行业需求旺盛' },
  { id: 5, time: '10:43:48', content: '医疗健康行业前景广阔' }
])

// 实时数据折线图配置
const dataStreamChartOption = computed(() => ({
  color: ['#52c41a'],
  tooltip: { trigger: 'axis' },
  grid: { left: 30, right: 20, top: 30, bottom: 30, containLabel: true },
  xAxis: {
    type: 'category',
    data: dataStream.value.map(item => item.time).reverse(),
    axisLine: { lineStyle: { color: '#52c41a' } },
    axisLabel: { color: '#fff' }
  },
  yAxis: {
    type: 'value',
    axisLine: { lineStyle: { color: '#52c41a' } },
    axisLabel: { color: '#fff' },
    splitLine: { lineStyle: { color: 'rgba(82,196,26,0.1)' } }
  },
  series: [
    {
      name: '实时数据',
      type: 'line',
      smooth: true,
      data: dataStream.value.map(item => parseFloat(item.value)).reverse(),
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: { width: 3 },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(82,196,26,0.3)' },
            { offset: 1, color: 'rgba(82,196,26,0.1)' }
          ]
        }
      }
    }
  ]
}))

// 就业去向分布数据
const employmentDestinationData = ref([
  { value: 3200, name: '国企' },
  { value: 2800, name: '民企' },
  { value: 1500, name: '外企' },
  { value: 1200, name: '升学' },
  { value: 800, name: '出国' }
])

// 就业去向分布饼图配置
const employmentDestinationOption = computed(() => ({
  color: ['#00cfff', '#52c41a', '#fa8c16', '#eb2f96', '#722ed1'],
  tooltip: { trigger: 'item' },
  legend: {
    orient: 'vertical',
    left: 'left',
    textStyle: { color: '#fff' }
  },
  series: [
    {
      name: '就业去向',
      type: 'pie',
      radius: ['30%', '70%'],
      center: ['60%', '55%'],
      roseType: 'area',
      data: employmentDestinationData.value,
      label: {
        show: true,
        position: 'outside',
        formatter: '{b}\n{d}%',
        color: '#fff'
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 20,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.7)'
        }
      },
      animationType: 'scale',
      animationEasing: 'elasticOut',
      animationDelay: (idx: any) => Math.random() * 200
    }
  ]
}))

// 学历结构分布数据（改为接口获取）
const degreeStructureData = ref<any[]>([])

// 获取学历结构分布数据
const fetchDegreeStructureData = () => {
  fetch('http://localhost:9908/api/studentInfo/educationRate')
    .then(res => res.json())
    .then(data => {
      if (data.code === 200) {
        degreeStructureData.value = data.data.map((item: any) => ({
          name: item.educationLevel,
          value: item.rate,
          total: item.totalLevelStudents
        }))
      }
    })
    .catch(err => {
      console.error('获取学历结构分布数据失败:', err)
    })
}

// 学历结构分布环形图配置（用接口数据）
const degreeStructureOption = computed(() => ({
  color: ['#00cfff', '#52c41a', '#fa8c16', '#eb2f96'],
  tooltip: {
    trigger: 'item',
    formatter: (params: any) => {
      const d = degreeStructureData.value[params.dataIndex]
      return `${d.name}<br/>占比: ${d.value}%<br/>人数: ${d.total.toLocaleString()}人`
    }
  },
  legend: {
    orient: 'vertical',
    left: 'left',
    textStyle: { color: '#fff' }
  },
  series: [
    {
      name: '学历结构',
      type: 'pie',
      radius: ['60%', '80%'],
      center: ['60%', '55%'],
      data: degreeStructureData.value,
      label: {
        show: true,
        position: 'outside',
        formatter: '{b}\n{d}%',
        color: '#fff'
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
}))

// 更新当前时间
const { pause: pauseTimeUpdate } = useIntervalFn(() => {
  currentTime.value = new Date().toLocaleString()
}, 1000, { immediate: true })

// 更新数据流
const { pause: pauseDataStream } = useIntervalFn(() => {
  const newData = {
    id: Date.now(),
    time: new Date().toLocaleTimeString('zh-CN', { hour12: false }),
    content: '新增就业数据',
    value: '+' + (Math.random() * 100).toFixed(1),
    type: 'up'
  }
  dataStream.value.unshift(newData)
  if (dataStream.value.length > 5) {
    dataStream.value.pop()
  }
}, 3000, { immediate: true })

// 初始化
onMounted(() => {
  // 加载地图 geojson
  fetch('https://geo.datav.aliyun.com/areas_v3/bound/100000_full.json')
    .then(response => response.json())
    .then(geoJson => {
      echarts.registerMap('china', geoJson)
      // 获取后端就业分布数据
      fetch('http://localhost:9908/api/overview/summaryStats')
        .then(res => res.json())
        .then(result => {
          if (result.code === 200) {
            // 处理 EmploymentOverview.vue 的 mapDataVO
            mapData.value = result.data.mapDataVO.map((item: any) => ({
              name: item.name,
              value: item.value
            }))
            chinaMapReady.value = true
          } else {
            console.error('Failed to fetch map data:', result.msg)
          }
        })
        .catch(error => {
          console.error('Error fetching map data:', error)
        })
    })
    .catch(error => {
      console.error('加载地图数据失败:', error)
    })
  fetchDegreeStructureData()
})

// 组件卸载时停止定时器
onUnmounted(() => {
  pauseTimeUpdate()
  pauseDataStream()
})
</script>

<style lang="scss" scoped>
.data-visualization-screen {
  width: 100%;
  height: 100vh;
  min-height: 100vh;
  background-color: #001529;
  color: #fff;
  overflow: hidden;
  position: relative;
  display: flex;
  flex-direction: column;
}

body {
  overflow-x: hidden;
}

.content-wrapper {
  flex: 1 1 0;
  display: flex;
  flex-direction: column;
  gap: 20px;
  height: 100%;
  min-height: 0;
  width: 100%;
}

.screen-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  background: rgba(0, 24, 51, 0.9);
  border: 1px solid #00a1ff;
  border-radius: 4px;
  box-shadow: 0 0 20px rgba(0, 161, 255, 0.1);

  .main-title {
    font-size: 24px;
    margin: 0;
    background: linear-gradient(to right, #fff, #00a1ff);
    -webkit-background-clip: text;
    color: transparent;
  }

  .subtitle {
    font-size: 14px;
    color: #00a1ff;
    margin-top: 4px;
  }

  .current-time, .update-info {
    font-size: 14px;
    color: #00a1ff;
  }
}

.screen-content, .grid-layout {
  display: grid;
  grid-template-columns: 24% 52% 24%;
  gap: 18px;
  width: 100%;
  height: 100%;
  min-height: 0;
  box-sizing: border-box;
}

.left-panel, .right-panel {
  min-width: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  height: 100%;
}
.center-panel {
  min-width: 0;
  width: 100%;
  padding: 0;
  display: flex;
  flex-direction: column;
  justify-content: stretch;
}
.map-container, .trend-container {
  flex: 1 1 0;
  min-height: 0;
  width: 100%;
  padding: 0;
}
.map-wrapper, .trend-chart {
  width: 100%;
  height: 100%;
  min-height: 0;
  position: relative;
}
.map-chart {
  width: 100%;
  height: 100%;
}

.panel-section {
  background: rgba(0, 24, 51, 0.9);
  border: 1px solid #00a1ff;
  border-radius: 4px;
  padding: 15px;
  box-shadow: 0 0 20px rgba(0, 161, 255, 0.1);
  display: flex;
  flex-direction: column;
  min-height: 0;
  flex: 1;
}

.section-title {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  color: #00a1ff;
  font-size: 16px;

  .title-icon {
    width: 4px;
    height: 16px;
    background: #00a1ff;
    margin-right: 8px;
    border-radius: 2px;
  }
}

.metrics-section {
  .metrics-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;
  }

  .metric-card {
    padding: 15px;
    text-align: center;
    background: rgba(0, 36, 77, 0.5);
    border-radius: 4px;
    border: 1px solid rgba(0, 161, 255, 0.3);
  }

  .metric-circle {
    .metric-value {
      font-size: 28px;
      font-weight: bold;
      color: #00a1ff;
    }

    .metric-label {
      font-size: 14px;
      color: #fff;
      margin-top: 8px;
    }

    .metric-change {
      font-size: 12px;
      margin-top: 4px;

      &.up { color: #52c41a; }
      &.down { color: #ff4d4f; }
    }
  }
}

.chart-container {
  flex: 1;
  min-height: 0;
  position: relative;

  .chart {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
  }
}

.data-stream {
  flex: 1;
  overflow-y: auto;

  .stream-item {
    display: flex;
    align-items: center;
    padding: 8px 0;
    border-bottom: 1px solid rgba(0, 161, 255, 0.1);

    &:last-child {
      border-bottom: none;
    }

    .stream-time {
      width: 80px;
      font-size: 12px;
      color: #00a1ff;
    }

    .stream-content {
      flex: 1;
      padding: 0 10px;
      font-size: 12px;
    }

    .stream-value {
      width: 60px;
      text-align: right;
      font-size: 12px;

      &.up { color: #52c41a; }
      &.down { color: #ff4d4f; }
      &.normal { color: #00a1ff; }
    }
  }
}

.industry-ranking {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;

  .ranking-item {
    display: flex;
    align-items: center;
    gap: 10px;

    .rank-number {
      width: 24px;
      height: 24px;
      border-radius: 50%;
      background: rgba(0, 161, 255, 0.1);
      color: #00a1ff;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 12px;
    }

    .industry-name {
      width: 80px;
      font-size: 14px;
    }

    .industry-progress {
      flex: 1;
      height: 6px;
      background: rgba(0, 161, 255, 0.1);
      border-radius: 3px;
      overflow: hidden;

      .progress-bar {
        height: 100%;
        background: #00a1ff;
        border-radius: 3px;
        transition: width 0.3s ease;
      }
    }

    .industry-value {
      width: 50px;
      text-align: right;
      font-size: 14px;
      color: #00a1ff;
    }
  }
}

.screen-footer {
  height: 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  background: rgba(0, 24, 51, 0.9);
  border: 1px solid #00a1ff;
  border-radius: 4px;
  font-size: 14px;
  color: #00a1ff;

  .status-indicator {
    display: inline-block;
    width: 8px;
    height: 8px;
    border-radius: 50%;
    margin-right: 8px;

    &.online {
      background: #52c41a;
      box-shadow: 0 0 10px rgba(82, 196, 26, 0.5);
    }
  }
}

/* 粒子效果 */
.particle-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.particle {
  position: absolute;
  width: 2px;
  height: 2px;
  background: #00d4ff;
  border-radius: 50%;
  animation: float linear infinite;
}

/* 动画效果 */
@keyframes float {
  0% {
    transform: translateY(100vh) rotate(0deg);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100px) rotate(360deg);
    opacity: 0;
  }
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 4px;
}

::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.1);
}

::-webkit-scrollbar-thumb {
  background: #00d4ff;
  border-radius: 2px;
}

::-webkit-scrollbar-thumb:hover {
  background: #ff6b9d;
}

/* 浮动导航面板 */
.floating-nav {
  position: fixed;
  top: 50%;
  left: 20px;
  transform: translateY(-50%);
  z-index: 1000;
  transition: all 0.3s ease;
}

.nav-toggle {
  width: 50px;
  height: 50px;
  background: rgba(0, 0, 0, 0.8);
  border: 2px solid #00d4ff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.4);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.nav-toggle:hover {
  background: rgba(0, 212, 255, 0.2);
  transform: scale(1.1);
}

.nav-icon {
  font-size: 20px;
  color: #00d4ff;
}

.nav-menu {
  position: absolute;
  left: 70px;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(0, 0, 0, 0.9);
  border: 1px solid #00d4ff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 8px 32px rgba(0, 212, 255, 0.3);
  backdrop-filter: blur(20px);
  min-width: 320px;
  animation: slideInLeft 0.3s ease;
}

.nav-title {
  font-size: 16px;
  font-weight: bold;
  color: #00d4ff;
  margin-bottom: 15px;
  text-align: center;
  border-bottom: 1px solid rgba(0, 212, 255, 0.3);
  padding-bottom: 10px;
}

.nav-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px 10px;
  background: rgba(0, 212, 255, 0.1);
  border: 1px solid rgba(0, 212, 255, 0.3);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 12px;
  color: #a0c4ff;
}

.nav-item:hover {
  background: rgba(0, 212, 255, 0.2);
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 212, 255, 0.4);
  color: #00d4ff;
}

.nav-item .el-icon {
  font-size: 24px;
  margin-bottom: 8px;
  color: #00d4ff;
}

/* 动画效果 */
@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-20px) translateY(-50%);
  }
  to {
    opacity: 1;
    transform: translateX(0) translateY(-50%);
  }
}

.left-panel, .right-panel {
  display: flex;
  flex-direction: column;
  gap: 18px;
  min-height: 0;
}
.user-distribution-section {
  .user-pie-chart {
    width: 100%;
    height: 100%;
  }
}
.data-destination-section {
  .data-destination-chart {
    width: 100%;
    height: 100%;
  }
}
.salary-section {
  .salary-chart {
    width: 100%;
    height: 100%;
  }
}
.industry-section {
  .industry-bar-chart {
    width: 100%;
    height: 100%;
  }
}

.wordcloud-chart {
  height: 100%;
}
.news-section {
  .news-list {
    max-height: 100px;
    overflow-y: auto;
    .news-item {
      display: flex;
      font-size: 13px;
      color: #fff;
      padding: 4px 0;
      .news-time { color: #00cfff; width: 60px; }
      .news-content { flex: 1; text-align: left; }
    }
  }
}

.degree-structure-section {
  .degree-structure-chart {
    width: 100%;
    height: 100%;
  }
}

@media (max-width: 1200px) {
  .screen-content {
    grid-template-columns: 1fr;
    min-width: 0;
  }
  .left-panel, .right-panel {
    display: none;
  }
}
</style>
