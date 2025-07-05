import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// 就业数据接口定义
export interface EmploymentRate {
  year: number
  rate: number
  graduateCount: number
  employedCount: number
}

export interface IndustryData {
  name: string
  value: number
  growth: number
}

export interface SalaryData {
  major: string
  avgSalary: number
  minSalary: number
  maxSalary: number
  count: number
}

export interface RegionData {
  region: string
  employmentRate: number
  avgSalary: number
  jobCount: number
}

export interface SkillDemand {
  skill: string
  demand: number
  trend: 'up' | 'down' | 'stable'
}

export const useEmploymentStore = defineStore('employment', () => {
  // 就业率数据
  const employmentRates = ref<EmploymentRate[]>([
    { year: 2019, rate: 85.2, graduateCount: 8340000, employedCount: 7105680 },
    { year: 2020, rate: 82.1, graduateCount: 8740000, employedCount: 7175740 },
    { year: 2021, rate: 84.8, graduateCount: 9090000, employedCount: 7708320 },
    { year: 2022, rate: 87.3, graduateCount: 10760000, employedCount: 9393480 },
    { year: 2023, rate: 89.1, graduateCount: 11580000, employedCount: 10317780 },
    { year: 2024, rate: 90.5, graduateCount: 11790000, employedCount: 10669950 }
  ])

// 行业分布数据
  const industryData = ref<IndustryData[]>([
    { name: 'IT/互联网', value: 28.5, growth: 12.3 },
    { name: '金融', value: 15.2, growth: 5.8 },
    { name: '教育', value: 12.8, growth: 8.1 },
    { name: '制造业', value: 11.6, growth: 3.2 },
    { name: '医疗健康', value: 9.4, growth: 15.7 },
    { name: '房地产', value: 7.3, growth: -2.1 },
    { name: '咨询服务', value: 6.8, growth: 9.5 },
    { name: '政府机关', value: 4.2, growth: 1.8 },
    { name: '其他', value: 4.2, growth: 6.2 },
    // 补充数据开始
    { name: '新能源', value: 5.6, growth: 22.5 },
    { name: '文化传媒', value: 4.9, growth: 7.6 },
    { name: '物流仓储', value: 4.5, growth: 6.9 },
    { name: '零售电商', value: 4.1, growth: 10.2 },
    { name: '餐饮服务', value: 3.8, growth: 4.3 },
    { name: '广告营销', value: 3.5, growth: 8.8 },
    { name: '农业', value: 3.2, growth: 5.1 },
    { name: '酒店旅游', value: 2.9, growth: -1.5 },
    { name: '法律法务', value: 2.7, growth: 6.4 },
    { name: '艺术设计', value: 2.4, growth: 7.9 }
    // 补充数据结束
  ])

// 薪资数据
  const salaryData = ref<SalaryData[]>([
    { major: '计算机科学与技术', avgSalary: 12800, minSalary: 8000, maxSalary: 25000, count: 156789 },
    { major: '软件工程', avgSalary: 12200, minSalary: 7500, maxSalary: 22000, count: 134567 },
    { major: '数据科学与大数据技术', avgSalary: 13500, minSalary: 9000, maxSalary: 28000, count: 89234 },
    { major: '人工智能', avgSalary: 14200, minSalary: 10000, maxSalary: 30000, count: 67890 },
    { major: '金融学', avgSalary: 9800, minSalary: 6000, maxSalary: 18000, count: 198765 },
    { major: '会计学', avgSalary: 7200, minSalary: 4500, maxSalary: 12000, count: 234567 },
    { major: '市场营销', avgSalary: 8500, minSalary: 5000, maxSalary: 15000, count: 178934 },
    { major: '机械工程', avgSalary: 8800, minSalary: 5500, maxSalary: 16000, count: 145678 },
    { major: '电子信息工程', avgSalary: 10200, minSalary: 6500, maxSalary: 18000, count: 123456 },
    { major: '临床医学', avgSalary: 11500, minSalary: 7000, maxSalary: 20000, count: 98765 },
    // 补充数据开始
    { major: '电气工程及其自动化', avgSalary: 9500, minSalary: 6000, maxSalary: 18500, count: 132456 },
    { major: '汉语言文学', avgSalary: 6800, minSalary: 4000, maxSalary: 13000, count: 213456 },
    { major: '建筑学', avgSalary: 10800, minSalary: 6500, maxSalary: 22000, count: 97654 },
    { major: '护理学', avgSalary: 8200, minSalary: 5000, maxSalary: 15000, count: 165432 },
    { major: '法学', avgSalary: 8000, minSalary: 4500, maxSalary: 16000, count: 187654 },
    { major: '环境科学与工程', avgSalary: 7800, minSalary: 4800, maxSalary: 14000, count: 112345 },
    { major: '信息与计算科学', avgSalary: 11000, minSalary: 7000, maxSalary: 20000, count: 109876 },
    { major: '工商管理', avgSalary: 8300, minSalary: 5200, maxSalary: 15500, count: 176543 },
    { major: '口腔医学', avgSalary: 13000, minSalary: 8000, maxSalary: 26000, count: 76543 },
    { major: '材料科学与工程', avgSalary: 8600, minSalary: 5300, maxSalary: 15500, count: 129876 }
    // 补充数据结束
  ])

  // 地区数据
  const regionData = ref<RegionData[]>([
    { region: '北京', employmentRate: 94.2, avgSalary: 13500, jobCount: 856734 },
    { region: '上海', employmentRate: 93.8, avgSalary: 12800, jobCount: 734562 },
    { region: '深圳', employmentRate: 92.5, avgSalary: 12200, jobCount: 567890 },
    { region: '广州', employmentRate: 91.3, avgSalary: 10500, jobCount: 456789 },
    { region: '杭州', employmentRate: 90.8, avgSalary: 11200, jobCount: 345678 },
    { region: '南京', employmentRate: 89.6, avgSalary: 9800, jobCount: 234567 },
    { region: '成都', employmentRate: 88.7, avgSalary: 8900, jobCount: 298765 },
    { region: '武汉', employmentRate: 87.9, avgSalary: 8200, jobCount: 267890 },
    { region: '西安', employmentRate: 86.4, avgSalary: 7800, jobCount: 189234 },
    { region: '重庆', employmentRate: 85.2, avgSalary: 7500, jobCount: 178923 }
  ])

  // 技能需求数据
  const skillDemand = ref<SkillDemand[]>([
    { skill: 'Python', demand: 89, trend: 'up' },
    { skill: 'Java', demand: 85, trend: 'stable' },
    { skill: 'JavaScript', demand: 82, trend: 'up' },
    { skill: '数据分析', demand: 78, trend: 'up' },
    { skill: '机器学习', demand: 75, trend: 'up' },
    { skill: 'React', demand: 72, trend: 'up' },
    { skill: 'Vue.js', demand: 68, trend: 'up' },
    { skill: 'SQL', demand: 85, trend: 'stable' },
    { skill: 'Excel', demand: 70, trend: 'stable' },
    { skill: '项目管理', demand: 65, trend: 'up' },
    { skill: '英语', demand: 80, trend: 'stable' },
    { skill: '沟通能力', demand: 90, trend: 'stable' }
  ])

  // 计算属性
  const latestEmploymentRate = computed(() => {
    return employmentRates.value[employmentRates.value.length - 1]
  })

  const totalGraduates = computed(() => {
    return employmentRates.value.reduce((sum, item) => sum + item.graduateCount, 0)
  })

  const averageSalary = computed(() => {
    const total = salaryData.value.reduce((sum, item) => sum + item.avgSalary * item.count, 0)
    const totalCount = salaryData.value.reduce((sum, item) => sum + item.count, 0)
    return Math.round(total / totalCount)
  })

  const topIndustries = computed(() => {
    return industryData.value
      .sort((a, b) => b.value - a.value)
      .slice(0, 5)
  })

  // 方法
  const getEmploymentTrend = () => {
    return employmentRates.value.map(item => ({
      year: item.year,
      rate: item.rate
    }))
  }

  const getIndustryGrowth = () => {
    return industryData.value
      .filter(item => item.growth > 0)
      .sort((a, b) => b.growth - a.growth)
  }

  const getTopSalaryMajors = (limit = 10) => {
    return salaryData.value
      .sort((a, b) => b.avgSalary - a.avgSalary)
      .slice(0, limit)
  }

  const getTopRegions = (limit = 10) => {
    return regionData.value
      .sort((a, b) => b.employmentRate - a.employmentRate)
      .slice(0, limit)
  }

  const getTopSkills = (limit = 10) => {
    return skillDemand.value
      .sort((a, b) => b.demand - a.demand)
      .slice(0, limit)
  }

  return {
    // 状态
    employmentRates,
    industryData,
    salaryData,
    regionData,
    skillDemand,

    // 计算属性
    latestEmploymentRate,
    totalGraduates,
    averageSalary,
    topIndustries,

    // 方法
    getEmploymentTrend,
    getIndustryGrowth,
    getTopSalaryMajors,
    getTopRegions,
    getTopSkills
  }
})
