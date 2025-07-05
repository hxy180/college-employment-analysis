<template>
  <div class="trend-analysis-container">
    <!-- 第一行：就业率和薪资趋势 -->
    <el-row :gutter="20" class="mb-20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span class="card-title">就业率趋势变化</span>
              <el-button-group>
                <el-button :type="employmentType === 'all' ? 'primary' : 'default'" @click="employmentType = 'all'">总体</el-button>
                <el-button :type="employmentType === 'grad' ? 'primary' : 'default'" @click="employmentType = 'grad'">应届生</el-button>
              </el-button-group>
            </div>
          </template>
          <div class="chart-container" ref="employmentTrendChart"></div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span class="card-title">薪资趋势变化</span>
              <el-button-group>
                <el-button :type="salaryTrendType === 'avg' ? 'primary' : 'default'" @click="salaryTrendType = 'avg'">平均薪资</el-button>
                <el-button :type="salaryTrendType === 'median' ? 'primary' : 'default'" @click="salaryTrendType = 'median'">中位数</el-button>
              </el-button-group>
            </div>
          </template>
          <div class="chart-container" ref="salaryTrendChart"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 第二行：新增的行业就业趋势 -->
    <el-row>
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span class="card-title">行业就业人数变化趋势</span>
              <el-select
                v-model="selectedIndustries"
                multiple
                placeholder="选择行业"
                style="width: 300px"
              >
                <el-option
                  v-for="item in allIndustries"
                  :key="item"
                  :label="item"
                  :value="item"
                />
              </el-select>
            </div>
          </template>
          <div class="chart-container" ref="industryTrendChart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch, nextTick } from 'vue';
import * as echarts from 'echarts';

// 图表实例
const employmentTrendChart = ref<HTMLElement | null>(null);
const salaryTrendChart = ref<HTMLElement | null>(null);
const industryTrendChart = ref<HTMLElement | null>(null);

// 状态管理
const employmentType = ref<'all' | 'grad'>('all');
const salaryTrendType = ref<'avg' | 'median'>('avg');
const allIndustries = ref<string[]>([
  '互联网', '金融', '教育', '医疗', '制造业', '零售',
  '物流', '能源', '建筑'
]);

// 默认全部选中
const selectedIndustries = ref<string[]>(allIndustries.value.slice());

// 就业率数据（精确匹配图片数值）
const employmentData = {
  all: [86, 87, 88, 89, 90, 91, 92, 93, 95, 97], // 百分比值
  grad: [66, 67, 68, 69, 70, 71, 72, 73, 75, 77],
  years: [2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2023, 2024]
};

// 薪资数据（精确匹配图片数值）
const salaryData = {
  avg: [9000, 9300, 9600, 10000, 10500, 10800, 11200, 11800, 12500, 13200],
  median: [8500, 8700, 9000, 9300, 9800, 10000, 10500, 11000, 11500, 12000],
  years: [2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2023, 2024]
};

// 行业数据
const industryData = {
  years: [2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022, 2023, 2024],
  series: [
    { name: '互联网', data: [1200, 1500, 1800, 2200, 2600, 3000, 3500, 4000, 4500, 5000] },
    { name: '金融', data: [1000, 1200, 1400, 1600, 1800, 2000, 2200, 2400, 2600, 2800] },
    { name: '教育', data: [800, 900, 1000, 1100, 1200, 1300, 1400, 1500, 1600, 1700] },
    { name: '医疗', data: [700, 800, 900, 1000, 1100, 1200, 1300, 1400, 1500, 1600] },
    { name: '制造业', data: [1500, 1600, 1700, 1800, 1900, 2000, 2100, 2200, 2300, 2400] },
    { name: '零售', data: [600, 700, 800, 900, 1000, 1100, 1200, 1300, 1400, 1500] },
    { name: '物流', data: [500, 600, 700, 800, 900, 1000, 1100, 1200, 1300, 1400] },
    { name: '能源', data: [400, 450, 500, 550, 600, 650, 700, 750, 800, 850] },
    { name: '建筑', data: [900, 950, 1000, 1050, 1100, 1150, 1200, 1250, 1300, 1350] }
  ]
};

// 更新就业率趋势图
const updateEmploymentTrendChart = () => {
  if (!employmentTrendChart.value) return;
  
  const chart = echarts.getInstanceByDom(employmentTrendChart.value) || 
                echarts.init(employmentTrendChart.value);
  
  const data = employmentType.value === 'all' ? employmentData.all : employmentData.grad;
  
  chart.setOption({
    tooltip: {
      trigger: 'axis',
      formatter: '{b}年<br/>{a}: {c}%'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: employmentData.years,
      axisLabel: {
        formatter: '{value}'
      }
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      interval: 20,
      axisLabel: {
        formatter: '{value}%'
      }
    },
    series: [{
      name: employmentType.value === 'all' ? '总体就业率' : '应届生就业率',
      type: 'line',
      smooth: true,
      data: data,
      lineStyle: {
        width: 3,
        color: '#1890ff'
      },
      itemStyle: {
        color: '#1890ff'
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(24, 144, 255, 0.5)' },
          { offset: 1, color: 'rgba(24, 144, 255, 0.1)' }
        ])
      }
    }]
  });
};

// 更新薪资趋势图
const updateSalaryTrendChart = () => {
  if (!salaryTrendChart.value) return;
  
  const chart = echarts.getInstanceByDom(salaryTrendChart.value) || 
                echarts.init(salaryTrendChart.value);
  
  const data = salaryTrendType.value === 'avg' ? salaryData.avg : salaryData.median;
  
  chart.setOption({
    tooltip: {
      trigger: 'axis',
      formatter: '{b}年<br/>{a}: ¥{c}'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: salaryData.years,
      axisLabel: {
        formatter: '{value}'
      }
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 18000,
      interval: 3000,
      axisLabel: {
        formatter: '¥{value}'
      }
    },
    series: [{
      name: salaryTrendType.value === 'avg' ? '平均薪资' : '薪资中位数',
      type: 'line',
      smooth: true,
      data: data,
      lineStyle: {
        width: 3,
        color: '#1890ff'
      },
      itemStyle: {
        color: '#1890ff'
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(24, 144, 255, 0.5)' },
          { offset: 1, color: 'rgba(24, 144, 255, 0.1)' }
        ])
      }
    }]
  });
};

// 更新行业趋势图
const updateIndustryTrendChart = () => {
  if (!industryTrendChart.value) return;
  
  const chart = echarts.getInstanceByDom(industryTrendChart.value) || 
                echarts.init(industryTrendChart.value);
  
  // 过滤选中的行业数据
  const filteredSeries = industryData.series.filter(item => 
    selectedIndustries.value.includes(item.name)
  );
  
  chart.setOption({
    tooltip: {
      trigger: 'axis',
      formatter: (params: any[]) => {
        let result = `${params[0].axisValue}年<br/>`;
        params.forEach((item: any) => {
          result += `${item.marker} ${item.seriesName}: ${item.value}人<br/>`;
        });
        return result;
      }
    },
    legend: {
      data: filteredSeries.map(item => item.name),
      top: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: industryData.years
    },
    yAxis: {
      type: 'value',
      name: '就业人数'
    },
    series: filteredSeries.map(item => ({
      name: item.name,
      type: 'line',
      smooth: true,
      data: item.data,
      lineStyle: {
        width: 3
      },
      itemStyle: {
        color: getIndustryColor(item.name)
      },
      areaStyle: {
        opacity: 0.1
      }
    }))
  });
};

// 行业颜色映射
const getIndustryColor = (industry: string) => {
  const colors: Record<string, string> = {
    '互联网': '#1890ff',
    '金融': '#722ed1',
    '教育': '#13c2c2',
    '医疗': '#52c41a',
    '制造业': '#fa8c16',
    '零售': '#f5222d'
  };
  return colors[industry] || '#1890ff';
};

// 监听状态变化
watch(employmentType, () => {
  nextTick(() => {
    updateEmploymentTrendChart();
  });
});

watch(salaryTrendType, () => {
  nextTick(() => {
    updateSalaryTrendChart();
  });
});

watch(selectedIndustries, () => {
  nextTick(() => {
    updateIndustryTrendChart();
  });
}, { deep: true });

// 初始化
onMounted(() => {
  updateEmploymentTrendChart();
  updateSalaryTrendChart();
  updateIndustryTrendChart();
  
  window.addEventListener('resize', () => {
    if (employmentTrendChart.value) echarts.getInstanceByDom(employmentTrendChart.value)?.resize();
    if (salaryTrendChart.value) echarts.getInstanceByDom(salaryTrendChart.value)?.resize();
    if (industryTrendChart.value) echarts.getInstanceByDom(industryTrendChart.value)?.resize();
  });
});
</script>

<style scoped>
.trend-analysis-container {
  padding: 20px;
  background-color: white;
}

.mb-20 {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  width: 100%;
  height: 300px;
  margin-top: 10px;
}

.el-button-group {
  margin-left: 10px;
}

.card-title {
  font-size: 16px;
  font-weight: bold;
}
</style>