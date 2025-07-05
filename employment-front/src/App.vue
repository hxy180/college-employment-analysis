<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  DataAnalysis,
  Monitor,
  TrendCharts,
  Money,
  Grid,
  Location,
  Document,
  DataLine,
  FolderOpened,
  QuestionFilled
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 判断是否为数据可视化大屏页面
const isDataVisualizationPage = computed(() => {
  return route.path === '/data-visualization'
})

// 面包屑导航
const currentBreadcrumb = computed(() => {
  const breadcrumbMap: Record<string, string> = {
    '/dashboard': '数据概览',
    '/salary-analysis': '薪资分析',
    '/industry-analysis': '行业分析',
    '/region-analysis': '地区分析',
    '/reports/employment': '就业报告',
    '/reports/trend': '趋势报告',
    '/data-management': '数据管理',
    '/data-visualization': '数据可视化大屏',
    '/help': '帮助中心'
  }
  return breadcrumbMap[route.path] || ''
})

// 返回数据可视化大屏
const goToDataVisualization = () => {
  router.push('/data-visualization')
}

// 刷新数据
const refreshData = () => {
  ElMessage.success('数据刷新成功')
}

// 显示设置
const showSettings = () => {
  ElMessage.info('设置功能开发中...')
}

// 用户菜单操作
const handleUserCommand = (command: string) => {
  if (command === 'profile') {
    ElMessage.info('个人信息功能开发中...')
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      ElMessage.success('已退出登录')
    })
  }
}

onMounted(() => {
  // 页面加载完成后的初始化操作
})
</script>

<template>
  <el-container class="app-container">
    <!-- 顶部导航栏 -->
    <el-header class="app-header" v-if="!isDataVisualizationPage">
      <div class="header-left">
        <div class="logo">
          <el-icon class="logo-icon"><DataAnalysis /></el-icon>
          <span class="logo-text">大学生就业大数据分析平台</span>
        </div>
      </div>
      <div class="header-right">
        <el-button text @click="goToDataVisualization">
          <el-icon><Monitor /></el-icon>
          返回大屏
        </el-button>
      </div>
    </el-header>

    <el-container v-if="!isDataVisualizationPage">
      <!-- 左侧菜单 -->
      <el-aside width="250px" class="app-aside">
        <el-menu
          :default-active="$route.path"
          class="sidebar-menu"
          router
          unique-opened
        >
          <el-sub-menu index="analysis">
            <template #title>
              <el-icon><TrendCharts /></el-icon>
              <span>深度分析</span>
            </template>
            <el-menu-item index="/depth-analysis/employment-overview">
              <el-icon><DataLine /></el-icon>
              <span>就业概况</span>
            </el-menu-item>
            <el-menu-item index="/depth-analysis/major-analysis">
              <el-icon><Grid /></el-icon>
              <span>专业分析</span>
            </el-menu-item>
            <el-menu-item index="/depth-analysis/region-analysis">
              <el-icon><Location /></el-icon>
              <span>地区分析</span>
            </el-menu-item>
            <el-menu-item index="/depth-analysis/salary-analysis">
              <el-icon><Money /></el-icon>
              <span>薪资分析</span>
            </el-menu-item>
            <el-menu-item index="/depth-analysis/trend-analysis">
              <el-icon><TrendCharts /></el-icon>
              <span>趋势分析</span>
            </el-menu-item>
            <el-menu-item index="/depth-analysis/industry-analysis">
              <el-icon><DataLine /></el-icon>
              <span>行业分析</span>
            </el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="reports">
            <template #title>
              <el-icon><Document /></el-icon>
              <span>报告中心</span>
            </template>
            <el-menu-item index="/reports/employment">
              <el-icon><DataLine /></el-icon>
              <span>就业报告</span>
            </el-menu-item>
            <el-menu-item index="/reports/trend">
              <el-icon><TrendCharts /></el-icon>
              <span>趋势报告</span>
            </el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/data-visualization">
            <el-icon><Monitor /></el-icon>
            <span>数据大屏</span>
          </el-menu-item>
          <el-menu-item index="/help">
            <el-icon><QuestionFilled /></el-icon>
            <span>帮助中心</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主要内容区域 -->
      <el-main class="app-main">
        <div class="breadcrumb-container">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item v-if="currentBreadcrumb">{{ currentBreadcrumb }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="main-content">
          <RouterView />
        </div>
      </el-main>
    </el-container>


    <!-- 数据可视化大屏单独渲染 -->
    <RouterView v-if="isDataVisualizationPage" class="fullscreen-view" />
  </el-container>
</template>

<style>
/* 全局基础样式 */
html, body, #app {
  width: 100vw;
  height: 100vh;
  min-width: 100vw;
  min-height: 100vh;
  margin: 0;
  padding: 0;
  background: #001529;
  overflow-x: hidden;
}

#app {
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.app-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.app-header {
  height: 60px;
  flex-shrink: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.el-container:not(.app-container) {
  flex: 1;
  overflow: hidden;
}

.app-aside {
  width: 250px;
  flex-shrink: 0;
  background-color: #f8f9fa;
  border-right: 1px solid #e9ecef;
  overflow-y: auto;
}

.app-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: auto;
  padding: 0;
  background-color: #001529;
  min-height: 100vh;
}

.breadcrumb-container {
  padding: 15px 20px;
  background: white;
  border-bottom: 1px solid #e9ecef;
  flex-shrink: 0;
}

.main-content {
  flex: 1;
  overflow: auto;
  position: relative;
  width: 100%;
  min-height: 100%;
  background: transparent;
}

/* 全屏视图样式 */
.fullscreen-view {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
  z-index: 999;
  background: #f5f7fa;
}

.header-left {
  display: flex;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: bold;
}

.logo-icon {
  font-size: 28px;
  margin-right: 10px;
  color: #FFD700;
}

.logo-text {
  color: white;
  font-weight: bold;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.user-dropdown:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.username {
  color: white;
  font-size: 14px;
}

.header-left, .header-right {
  font-weight: bold;
}

.header-right .el-button {
  font-weight: bold;
}
</style>
