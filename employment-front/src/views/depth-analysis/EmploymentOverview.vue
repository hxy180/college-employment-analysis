<template>
  <div class="employment-overview-container">
    <h1 class="page-title">就业概况分析</h1>
    <div class="dashboard">
      <!-- 数据卡片 -->
      <div class="card col-3">
        <div class="stats-card">
          <div class="stat-label">总毕业人数</div>
          <div class="stat-value">{{ stats.totalGraduates }}</div>
        </div>
      </div>

      <div class="card col-3">
        <div class="stats-card">
          <div class="stat-label">就业人数</div>
          <div class="stat-value">{{ stats.employed }}</div>
        </div>
      </div>

      <div class="card col-3">
        <div class="stats-card">
          <div class="stat-label">就业率</div>
          <div class="stat-value">{{ stats.employmentRate }}%</div>
        </div>
      </div>

      <div class="card col-3">
        <div class="stats-card">
          <div class="stat-label">平均薪资</div>
          <div class="stat-value">¥{{ stats.avgSalary }}</div>
        </div>
      </div>

      <!-- 各学历就业率对比 -->
      <div class="card col-6">
        <div class="card-header">
          <div class="card-title">各学历就业率对比</div>
        </div>
        <div class="chart-container" ref="educationChart"></div>
      </div>

      <!-- 行业就业占比 -->
      <div class="card col-6">
        <div class="card-header">
          <div class="card-title">行业就业占比</div>
        </div>
        <div class="chart-container" ref="industryChart"></div>
      </div>

      <!-- 全国就业分布 -->
      <div class="card col-12">
        <div class="card-header">
          <div class="card-title">全国就业分布热力图</div>
        </div>
        <div class="chart-container" ref="mapChart"></div>
      </div>
    </div>

    <!-- 功能导航模块补全所有侧边栏对应项 -->
    <ul class="function-nav-list">
      <li><el-icon><DataLine /></el-icon> 就业概况</li>
      <li><el-icon><Grid /></el-icon> 专业分析</li>
      <li><el-icon><Location /></el-icon> 地区分析</li>
      <li><el-icon><Money /></el-icon> 薪资分析</li>
      <li><el-icon><TrendCharts /></el-icon> 趋势分析</li>
      <li><el-icon><DataLine /></el-icon> 行业分析</li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue';
import * as echarts from 'echarts';

const educationChart = ref<HTMLDivElement | null>(null);
const industryChart = ref<HTMLDivElement | null>(null);
const mapChart = ref<HTMLDivElement | null>(null);

const stats = reactive({
  totalGraduates: 0,
  employed: 0,
  employmentRate: 0,
  avgSalary: 0
});

const educationData = ref([]);
const industryData = ref([]);
const mapData = ref([]);

onMounted(async () => {
  try {
    const response = await fetch('http://localhost:9908/api/overview/summaryStats');
    const result = await response.json();
    if (result.code === 200) {
      const data = result.data;
      // 处理总体统计数据
      stats.totalGraduates = data.overviewStatsVO.totalGraduates;
      stats.employed = data.overviewStatsVO.employed;
      stats.employmentRate = data.overviewStatsVO.employmentRate;
      stats.avgSalary = data.overviewStatsVO.avgSalary;

      // 处理学历就业率数据
      educationData.value = data.educationLevelRateVO.map(item => ({
        name: item.level,
        rate: item.rate
      }));

      // 处理行业分布数据
      industryData.value = data.industryDistributionVO;

      // 处理地图数据
      mapData.value = data.mapDataVO.map(item => ({
        name: item.name,
        value: item.value
      }));

      // 初始化图表
      initEducationChart();
      initIndustryChart();
      initMapChart();
    } else {
      console.error('Failed to fetch data:', result.msg);
    }
  } catch (error) {
    console.error('Error fetching data:', error);
  }
});

// 初始化学历就业率图表
const initEducationChart = () => {
  if (!educationChart.value) return;
  const chart = echarts.init(educationChart.value);
  chart.setOption({
    color: ['#1890ff'],
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: educationData.value.map(item => item.name) },
    yAxis: { type: 'value', max: 100, axisLabel: { formatter: '{value}%' } },
    series: [{
      name: '就业率',
      type: 'bar',
      data: educationData.value.map(item => item.rate),
      itemStyle: { borderRadius: [4, 4, 0, 0] }
    }]
  });
  window.addEventListener('resize', () => chart.resize());
};

// 初始化行业就业占比图表
const initIndustryChart = () => {
  if (!industryChart.value) return;
  const chart = echarts.init(industryChart.value);
  chart.setOption({
    color: ['#1890ff', '#52c41a', '#fa8c16', '#f5222d', '#722ed1', '#eb2f96'],
    tooltip: { trigger: 'item' },
    series: [{
      name: '行业分布',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 4, borderColor: '#fff', borderWidth: 2 },
      label: { show: false, position: 'center' },
      emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
      labelLine: { show: false },
      data: industryData.value
    }]
  });
  window.addEventListener('resize', () => chart.resize());
};

// 初始化地图图表
const initMapChart = () => {
  if (!mapChart.value) return;
  fetch('https://geo.datav.aliyun.com/areas_v3/bound/100000_full.json')
    .then(res => res.json())
    .then(geoJson => {
      echarts.registerMap('china', geoJson);
      const chart = echarts.init(mapChart.value!);
      chart.setOption({
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
      });
      window.addEventListener('resize', () => chart.resize());
    });
};
</script>

<style scoped>
.employment-overview-container {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: 100vh;
  height: 100vh;
  overflow-y: auto;
}

.page-title {
  margin-bottom: 24px;
  color: #1a1a1a;
  font-size: 24px;
  font-weight: 600;
  text-align: center;
}

.dashboard {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  gap: 20px;
  width: 100%;
}

.card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.col-3 {
  grid-column: span 3;
}

.col-6 {
  grid-column: span 6;
}

.col-8 {
  grid-column: span 8;
}

.col-4 {
  grid-column: span 4;
}

.col-12 {
  grid-column: span 12;
}

.card-header {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 500;
  color: #1a1a1a;
}

.chart-container {
  width: 100%;
  height: 300px;
  padding: 16px;
}

.stats-card {
  padding: 20px;
  text-align: center;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
}

@media screen and (max-width: 1200px) {
  .col-3, .col-4, .col-6, .col-8 {
    grid-column: span 12;
  }
}

/* 动态背景效果 */
.grid-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: linear-gradient(rgba(20, 20, 20, 0.05) 1px, transparent 1px),
                    linear-gradient(90deg, rgba(20, 20, 20, 0.05) 1px, transparent 1px);
  background-size: 20px 20px;
  z-index: -1;
  opacity: 0.5;
}

div.data-flow {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: -1;
}

div.data-flow::before {
  content: '';
  position: absolute;
  width: 100%;
  height: 100%;
  background: linear-gradient(to bottom, transparent, rgba(18, 18, 18, 0.02));
}

/* 卡片标签样式 */
.el-tag {
  margin-right: 5px;
}

/* 表格样式 */
.el-table {
  width: 100%;
}

/* 进度条样式 */
.el-progress {
  width: 120px;
}

/* 分页样式 */
.pagination-container {
  margin-top: 16px;
  text-align: right;
}

/* 搜索框和筛选器样式 */
.table-controls {
  display: flex;
  gap: 10px;
}

.search-input {
  width: 200px;
}

.filter-select {
  width: 150px;
}

/* 选项卡样式 */
.card-tabs {
  display: flex;
  gap: 10px;
}

.tab-item {
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  cursor: pointer;
}

.tab-item.active {
  background-color: #1890ff;
  color: white;
}

/* 趋势摘要样式 */
.trend-summary {
  display: flex;
  justify-content: space-around;
  padding: 16px;
  border-top: 1px solid #f0f0f0;
}

.trend-item {
  text-align: center;
}

.trend-item .label {
  font-size: 12px;
  color: #666;
}

.trend-item .value {
  font-size: 16px;
  font-weight: 500;
}

.up {
  color: #f5222d;
}

down {
  color: #52c41a;
}

/* 功能导航模块样式 */
.function-nav-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  justify-content: space-around;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 16px;
}

.function-nav-list li {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
}

.function-nav-list li:hover {
  color: #1890ff;
}

.function-nav-list li .el-icon {
  font-size: 20px;
  margin-bottom: 8px;
}
</style>
