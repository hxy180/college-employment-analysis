<template>
  <div class="major-analysis-container">
    <h1 class="page-title">专业分析</h1>
    <!-- 动态背景 -->
    <div class="grid-bg"></div>
    <div class="data-flow"></div>

    <div class="dashboard">
      <!-- 专业就业率TOP10 -->
      <div class="card col-6">
        <div class="card-header">
          <div class="card-title">专业就业率TOP10</div>
          <div class="card-tabs">
            <div
              class="tab-item"
              :class="{ 'active': state.employmentTab === 'top' }"
              @click="state.employmentTab = 'top'"
            >TOP10</div>
            <div
              class="tab-item"
              :class="{ 'active': state.employmentTab === 'bottom' }"
              @click="state.employmentTab = 'bottom'"
            >BOTTOM10</div>
          </div>
        </div>
        <div class="chart-container" ref="employmentChart"></div>
      </div>

      <!-- 专业平均薪资 -->
      <div class="card col-6">
        <div class="card-header">
          <div class="card-title">专业平均薪资</div>
        </div>
        <div class="chart-container" ref="salaryChart"></div>
      </div>

      <!-- 专业与行业流向桑基图 -->
      <div class="card col-12">
        <div class="card-header">
          <div class="card-title">专业与行业就业流向</div>
        </div>
        <div class="chart-container" ref="sankeyChart"></div>
      </div>

      <!-- 专业详情表格 -->
      <div class="card col-12">
        <div class="card-header">
          <div class="card-title">专业详情数据</div>
          <div class="table-controls">
            <el-input
                    v-model="search"
                    size="small"
                    placeholder="搜索专业名称"
                    class="search-input"
            />
            <el-select v-model="filterField" placeholder="筛选字段" size="small" class="filter-select">
              <el-option label="所有字段" value="all"></el-option>
              <el-option label="专业名称" value="major"></el-option>
              <el-option label="就业率" value="employmentRate"></el-option>
              <el-option label="平均薪资" value="avgSalary"></el-option>
            </el-select>
          </div>
        </div>
        <el-table
                :data="filteredTableData"
                style="width: 100%"
                height="400"
                stripe
                highlight-current-row
                @sort-change="handleSortChange"
        >
          <el-table-column
                  prop="major"
                  label="专业名称"
                  sortable="custom"
                  width="200"
          />
          <el-table-column
                  prop="employmentRate"
                  label="就业率 (%)"
                  sortable="custom"
                  width="120"
          >
            <template #default="scope">
              <div class="progress-cell">
                <el-progress
                        :percentage="scope.row.employmentRate"
                        :color="getProgressColor(scope.row.employmentRate)"
                        :show-text="false"
                />
                <span>{{ scope.row.employmentRate }}%</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column
                  prop="avgSalary"
                  label="平均薪资 (¥)"
                  sortable="custom"
                  width="150"
          >
            <template #default="scope">
              <span class="salary-value">¥{{ scope.row.avgSalary.toLocaleString() }}</span>
            </template>
          </el-table-column>
          <el-table-column
                  prop="employmentCount"
                  label="就业人数"
                  sortable="custom"
                  width="120"
          />
          <el-table-column
                  prop="topIndustry"
                  label="主要就业行业"
          />
          <el-table-column
                  prop="industryPercent"
                  label="行业占比 (%)"
                  width="120"
          />
        </el-table>
        <div class="pagination-container">
          <el-pagination
                  v-model:currentPage="currentPage"
                  :page-size="pageSize"
                  layout="prev, pager, next, jumper"
                  :total="filteredTableData.length"
                  background
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, computed, watch } from 'vue';
import { useEmploymentStore } from '@/stores/employment';
import * as echarts from 'echarts/core';
import { BarChart, SankeyChart } from 'echarts/charts';
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components';
import { CanvasRenderer } from 'echarts/renderers';

echarts.use([BarChart, SankeyChart, GridComponent, TooltipComponent, LegendComponent, CanvasRenderer]);
import { useRouter } from 'vue-router';
import { ElInput, ElSelect, ElOption, ElTable, ElTableColumn, ElProgress, ElPagination } from 'element-plus';

const router = useRouter();
const employmentStore = useEmploymentStore();

// 图表引用
const employmentChart = ref<HTMLDivElement>(null);
const salaryChart = ref<HTMLDivElement>(null);
const sankeyChart = ref<HTMLDivElement>(null);

// 状态管理
const topEmploymentData = ref([]);
const bottomEmploymentData = ref([]);

const state = reactive({
  employmentTab: 'top',
  search: '',
  filterField: 'all',
  currentPage: 1,
  pageSize: 10,
  sortField: '',
  sortOrder: '',
  tableData: [] as any[],
  salaryData: [] as any[],
  sankeyData: { nodes: [], links: [] } as any
});

// 计算属性 - 筛选后的表格数据
const filteredTableData = computed(() => {
  let result = [...state.tableData];

  // 搜索过滤
  if (state.search) {
    const searchTerm = state.search.toLowerCase();
    result = result.filter(item => {
      if (state.filterField === 'all') {
        return Object.values(item).some(val =>
          val.toString().toLowerCase().includes(searchTerm)
        );
      } else if (state.filterField === 'major') {
        return item.major.toLowerCase().includes(searchTerm);
      } else if (state.filterField === 'employmentRate') {
        return item.employmentRate.toString().includes(searchTerm);
      } else if (state.filterField === 'avgSalary') {
        return item.avgSalary.toString().includes(searchTerm);
      }
      return true;
    });
  }

  // 排序
  if (state.sortField) {
    result.sort((a, b) => {
      if (a[state.sortField!] < b[state.sortField!]) {
        return state.sortOrder === 'ascending' ? -1 : 1;
      }
      if (a[state.sortField!] > b[state.sortField!]) {
        return state.sortOrder === 'ascending' ? 1 : -1;
      }
      return 0;
    });
  }

  return result;
});

// 获取进度条颜色
const getProgressColor = (percentage: number) => {
  if (percentage >= 90) return '#52c41a';
  if (percentage >= 80) return '#faad14';
  return '#f5222d';
};

// 处理排序变化
const handleSortChange = (sort: any) => {
  state.sortField = sort.prop;
  state.sortOrder = sort.order;
};

// 导航到其他页面
const navigate = (path: string) => {
  router.push(`/depth-analysis/${path}`);
};

// 初始化学就业率图表
const initEmploymentChart = () => {
  if (!employmentChart.value) return;
  const chart = echarts.init(employmentChart.value);

  // 根据选项卡显示TOP10或BOTTOM10
  const displayData = state.employmentTab === 'top'
    ? topEmploymentData.value
    : bottomEmploymentData.value;

  chart.setOption({
    color: ['#1890ff'],
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '10%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'value', max: 100, axisLabel: { formatter: '{value}%' } },
    yAxis: {
      type: 'category',
      data: displayData.map(item => item.major),
      axisLabel: { interval: 0, rotate: 30 }
    },
    series: [{
      name: '就业率',
      type: 'bar',
      data: displayData.map(item => item.employmentRate),
      itemStyle: { borderRadius: [0, 4, 4, 0] },
      label: { show: true, position: 'right', formatter: '{c}%' }
    }]
  });

  window.addEventListener('resize', () => chart.resize());
};

// 初始化薪资图表
const initSalaryChart = () => {
  if (!salaryChart.value) return;
  const chart = echarts.init(salaryChart.value);

  // 按薪资排序取前15个专业
  const sortedData = [...state.salaryData].sort((a, b) => b.avgSalary - a.avgSalary).slice(0, 15);

  chart.setOption({
    color: ['#fa8c16'],
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true },
    xAxis: {
      type: 'category',
      data: sortedData.map(item => item.major),
      axisLabel: { interval: 0, rotate: 45 }
    },
    yAxis: { type: 'value', axisLabel: { formatter: '¥{value}' } },
    series: [{
      name: '平均薪资',
      type: 'bar',
      data: sortedData.map(item => item.avgSalary),
      itemStyle: { borderRadius: [4, 4, 0, 0] },
      label: { show: true, position: 'top', formatter: '¥{c}' }
    }]
  });

  window.addEventListener('resize', () => chart.resize());
};

// 初始化桑基图
const initSankeyChart = () => {
  if (!sankeyChart.value) return;
  const chart = echarts.init(sankeyChart.value);

  chart.setOption({
    tooltip: { trigger: 'item', triggerOn: 'mousemove' },
    series: [{
      type: 'sankey',
      left: 50,
      right: 50,
      data: state.sankeyData.nodes,
      links: state.sankeyData.links,
      emphasis: { focus: 'adjacency' },
      lineStyle: {
        color: 'gradient',
        curveness: 0.5
      },
      label: { fontSize: 12 }
    }]
  });

  window.addEventListener('resize', () => chart.resize());
};

onMounted(async () => {
  try {
    // 获取TOP10和BOTTOM10数据
    const [topResponse, bottomResponse] = await Promise.all([
      fetch('http://localhost:9908/api/summaryMajor/employment/top10'),
      fetch('http://localhost:9908/api/summaryMajor/employment/bottom10')
    ]);

    const [topResult, bottomResult] = await Promise.all([
      topResponse.json(),
      bottomResponse.json()
    ]);

    if (topResult.code === 200 && bottomResult.code === 200) {
      // 处理TOP10数据：去重、转换百分比并排序
      const uniqueTopData = Array.from(new Map(topResult.data.map((item: any) => [item.major, item])).values());
      topEmploymentData.value = uniqueTopData
        .map(item => ({
          major: item.major,
          employmentRate: Number((item.employmentRate).toFixed(1))
        }))
        .sort((a, b) => b.employmentRate - a.employmentRate);

      // 处理BOTTOM10数据：去重、转换百分比并排序
      const uniqueBottomData = Array.from(new Map(bottomResult.data.map((item: any) => [item.major, item])).values());
      bottomEmploymentData.value = uniqueBottomData
        .map(item => ({
          major: item.major,
          employmentRate: Number((item.employmentRate ).toFixed(1))
        }))
        .sort((a, b) => a.employmentRate - b.employmentRate);

      // 初始化就业率图表
      initEmploymentChart();
    } else {
      console.error('Failed to fetch employment data');
    }
  } catch (error) {
    console.error('Error fetching employment data:', error);
  }

  // 从API获取表格数据
      fetch('http://localhost:9908/api/summaryMajor/table/list')
        .then(response => response.json())
        .then(data => {
          if (data.code === 200) {
            // 去重处理
            const uniqueList = Array.from(new Map(data.data.list.map(item => [item.major, item])).values());
            // 格式化数据
            state.tableData = uniqueList.map(item => ({
              ...item,
              employmentRate: Math.round(item.employmentRate * 10.0) / 10, // 转换为百分比并保留一位小数
              avgSalary: Math.round(item.avgSalary ) // 转换为元并取整
            }));
          }
        })
        .catch(error => {
          console.error('获取表格数据失败:', error);
        });



    // 从API获取薪资数据
    fetch('http://localhost:9908/api/summaryMajor/salary/list')
      .then(response => response.json())
      .then(data => {
        if (data.code === 200) {
          // 去重处理
          const uniqueData = Array.from(new Map(data.data.map(item => [item.major, item])).values());
          // 转换薪资单位（假设API返回分，转换为元）并排序
          state.salaryData = uniqueData
            .map(item => ({
              ...item,
              avgSalary: Math.round(item.avgSalary) // 转换为元并取整
            }))
            .sort((a, b) => b.avgSalary - a.avgSalary);
          initSalaryChart();
        }
      })
      .catch(error => {
        console.error('获取薪资数据失败:', error);
      });

  // 桑基图数据（保留模拟数据）
  state.sankeyData = {
    nodes: [
      // 专业节点
      { name: '人工智能' }, { name: '数据科学' }, { name: '软件工程' }, { name: '信息安全' }, { name: '物联网工程' },
      { name: '金融科技' }, { name: '生物医学' }, { name: '电子工程' }, { name: '智能制造' }, { name: '新能源' },
      { name: '环境工程' }, { name: '材料科学' }, { name: '工商管理' }, { name: '市场营销' }, { name: '国际贸易' },
      { name: '汉语言文学' }, { name: '英语' }, { name: '法学' }, { name: '心理学' }, { name: '计算机' }, { name: '教育学' },

      // 行业节点
      { name: '信息技术' }, { name: '金融服务' }, { name: '教育科研' }, { name: '医疗健康' }, { name: '制造业' },
      { name: '文化传媒' }, { name: '建筑房地产' }, { name: '物流运输' }, { name: '政府机构' }, { name: '零售业' }
    ],
    links: [
      { source: '人工智能', target: '信息技术', value: 38000 },
      { source: '人工智能', target: '制造业', value: 26000 },
      { source: '人工智能', target: '智能制造', value: 20000 },
      { source: '人工智能', target: '教育科研', value: 20000 },

      { source: '数据科学', target: '信息技术', value: 34000 },
      { source: '数据科学', target: '金融服务', value: 21000 },
      { source: '数据科学', target: '教育科研', value: 18000 },

      { source: '软件工程', target: '信息技术', value: 39500 },
      { source: '软件工程', target: '教育科研', value: 12000 },
      { source: '软件工程', target: '金融服务', value: 16000 },
      { source: '软件工程', target: '智能制造', value: 16000 },

      { source: '信息安全', target: '信息技术', value: 36000 },
      { source: '信息安全', target: '政府机构', value: 19000 },
      { source: '信息安全', target: '金融服务', value: 21000 },

      { source: '物联网工程', target: '制造业', value: 24000 },
      { source: '物联网工程', target: '信息技术', value: 29000 },
      { source: '物联网工程', target: '物流运输', value: 12000 },
      { source: '物联网工程', target: '智能制造', value: 12000 },

      { source: '金融科技', target: '金融服务', value: 38000 },
      { source: '金融科技', target: '信息技术', value: 16000 },
      { source: '金融科技', target: '零售业', value: 11000 },
      { source: '金融科技', target: '国际贸易', value: 11000 },

      { source: '生物医学', target: '医疗健康', value: 40000 },
      { source: '生物医学', target: '科研教育', value: 16000 },
      { source: '生物医学', target: '政府机构', value: 10000 },

      { source: '电子工程', target: '制造业', value: 30000 },
      { source: '电子工程', target: '信息技术', value: 20000 },
      { source: '电子工程', target: '电子制造', value: 18000 },
      { source: '电子工程', target: '智能制造', value: 18000 },


      { source: '新能源', target: '制造业', value: 27000 },
      { source: '新能源', target: '政府机构', value: 15000 },
      { source: '新能源', target: '建筑房地产', value: 13000 },

      { source: '环境工程', target: '政府机构', value: 20000 },
      { source: '环境工程', target: '制造业', value: 17000 },
      { source: '环境工程', target: '建筑房地产', value: 15000 },

      { source: '材料科学', target: '制造业', value: 24000 },
      { source: '材料科学', target: '科研教育', value: 13000 },
      { source: '材料科学', target: '建筑房地产', value: 16000 },

      { source: '工商管理', target: '零售业', value: 22000 },
      { source: '工商管理', target: '金融服务', value: 21000 },
      { source: '工商管理', target: '文化传媒', value: 14000 },
      { source: '工商管理', target: '国际贸易', value: 14000 },

      { source: '市场营销', target: '文化传媒', value: 24000 },
      { source: '市场营销', target: '零售业', value: 19000 },
      { source: '市场营销', target: '金融服务', value: 17000 },
      { source: '市场营销', target: '国际贸易', value: 17000 },


      { source: '汉语言文学', target: '文化传媒', value: 23000 },
      { source: '汉语言文学', target: '教育科研', value: 20000 },
      { source: '汉语言文学', target: '政府机构', value: 13000 },

      { source: '英语', target: '教育科研', value: 22000 },
      { source: '英语', target: '文化传媒', value: 18000 },
      { source: '英语', target: '国际贸易', value: 16000 },

      { source: '法学', target: '政府机构', value: 30000 },
      { source: '法学', target: '金融服务', value: 17000 },
      { source: '法学', target: '教育科研', value: 11000 },
      { source: '法学', target: '国际贸易', value: 11000 },

      { source: '心理学', target: '医疗健康', value: 20000 },
      { source: '心理学', target: '教育科研', value: 18000 },
      { source: '心理学', target: '文化传媒', value: 10000 },

      { source: '计算机', target: '信息技术', value: 40000 },
      { source: '计算机', target: '教育科研', value: 15000 },
      { source: '计算机', target: '制造业', value: 18000 },
      { source: '计算机', target: '智能制造', value: 18000 },

      { source: '教育学', target: '教育科研', value: 40000 },
      { source: '教育学', target: '政府机构', value: 15000 },
      { source: '教育学', target: '文化传媒', value: 12000 }
    ]
  };



  console.log("生成的桑基图数据：", state.sankeyData);

  // 初始化其他图表
  initSalaryChart();
  initSankeyChart();
});

// 监听选项卡变化重新渲染图表
watch(() => state.employmentTab, initEmploymentChart);
</script>

<style scoped>
.major-analysis-container {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: 100vh;
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

.col-6 {
  grid-column: span 6;
}

.col-12 {
  grid-column: span 12;
}

.chart-container {
  width: 100%;
  height: 400px;
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
  height: 400px;
  padding: 16px;
}

/* 表格样式 */
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

.pagination-container {
  margin-top: 16px;
  text-align: right;
}

.progress-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.salary-value {
  color: #fa8c16;
  font-weight: 500;
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

@media screen and (max-width: 1200px) {
  .col-6 {
    grid-column: span 12;
  }
}

/* 动画效果 */
@keyframes float {
  0% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
  100% { transform: translateY(0px); }
}

.card {
  transition: all 0.3s ease;
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

/* 骨架屏样式 */
.skeleton {
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: loading 1.5s infinite;
}

@keyframes loading {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}
</style>
