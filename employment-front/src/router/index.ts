import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/data-visualization'
    },
    {
      path: '/depth-analysis',
      name: 'DepthAnalysis',
      meta: {
        title: '深度分析'
      },
      children: [
        {
          path: 'employment-overview',
          name: 'EmploymentOverview',
          component: () => import('../views/depth-analysis/EmploymentOverview.vue'),
          meta: { title: '就业概况' }
        },
        {
          path: 'major-analysis',
          name: 'MajorAnalysis',
          component: () => import('../views/depth-analysis/MajorAnalysis.vue'),
          meta: { title: '专业分析' }
        },
        {
          path: 'region-analysis',
          name: 'RegionAnalysis',
          component: () => import('../views/depth-analysis/RegionAnalysis.vue'),
          meta: { title: '地区分析' }
        },
        {
          path: 'salary-analysis',
          name: 'SalaryAnalysis',
          component: () => import('../views/depth-analysis/SalaryAnalysis.vue'),
          meta: { title: '薪资分析' }
        },
        {
          path: 'trend-analysis',
          name: 'TrendAnalysis',
          component: () => import('../views/depth-analysis/TrendAnalysis.vue'),
          meta: { title: '趋势分析' }
        },
        {
          path: 'industry-analysis',
          name: 'IndustryAnalysis',
          component: () => import('../views/depth-analysis/IndustryAnalysis.vue'),
          meta: { title: '行业分析' }
        }
      ]
    },
    {
      path: '/reports',
      name: 'Reports',
      children: [
        {
          path: 'employment',
          name: 'EmploymentReport',
          component: () => import('../views/reports/EmploymentReport.vue'),
          meta: {
            title: '就业报告'
          }
        },
        {
          path: 'trend',
          name: 'TrendReport',
          component: () => import('../views/reports/TrendReport.vue'),
          meta: {
            title: '趋势报告'
          }
        }
      ]
    },
    {
      path: '/help',
      name: 'Help',
      component: () => import('../views/Help.vue'),
      meta: {
        title: '帮助中心'
      }
    },
    {
      path: '/data-visualization',
      name: 'DataVisualization',
      component: () => import('../views/DataVisualization.vue'),
      meta: {
        title: '数据可视化大屏'
      }
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: () => import('../views/NotFound.vue'),
      meta: {
        title: '页面未找到'
      }
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 大学生就业大数据分析平台`
  }
  next()
})

export default router
