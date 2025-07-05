const { createApp, ref, onMounted } = Vue;

createApp({
    setup() {
        const stats = ref({
            totalGraduates: 0,
            employed: 0,
            employmentRate: 0,
            avgSalary: 0
        });

        // 创建数据粒子流
        const createParticleFlow = () => {
            const container = document.createElement('div');
            container.className = 'data-flow';
            document.body.appendChild(container);
            const particleCount = 50;

            for (let i = 0; i < particleCount; i++) {
                const particle = document.createElement('div');
                particle.classList.add('particle');

                // 随机大小
                const size = Math.random() * 4 + 1;
                particle.style.width = `${size}px`;
                particle.style.height = `${size}px`;

                // 随机位置
                const startLeft = Math.random() * 100;
                particle.style.left = `${startLeft}%`;
                particle.style.bottom = `-${size}px`;

                // 随机颜色
                const hue = Math.random() * 60 + 180; // 蓝色到紫色范围
                particle.style.background = `hsl(${hue}, 100%, 70%)`;

                // 随机动画时长
                const duration = Math.random() * 10 + 10;
                particle.style.animation = `float ${duration}s linear infinite`;

                // 随机延迟
                const delay = Math.random() * 5;
                particle.style.animationDelay = `${delay}s`;

                container.appendChild(particle);
            }
        };

        // 初始化图表
        const initCharts = async () => {
            // 模拟从API获取数据
            const mockData = {
                stats: {
                    totalGraduates: 8642,
                    employed: 7328,
                    employmentRate: 84.8,
                    avgSalary: 8450
                },
                educationLevels: [
                    { level: '博士', rate: 96.5 },
                    { level: '硕士', rate: 91.2 },
                    { level: '本科', rate: 85.3 },
                    { level: '专科', rate: 78.6 }
                ],
                industryDistribution: [
                    { value: 28.6, name: '信息技术' },
                    { value: 22.1, name: '金融服务' },
                    { value: 18.3, name: '教育科研' },
                    { value: 15.4, name: '医疗健康' },
                    { value: 8.2, name: '制造业' },
                    { value: 7.4, name: '其他' }
                ],
                mapData: [
                    {name: '北京', value: 3452},
                    {name: '上海', value: 3120},
                    {name: '广东', value: 2850},
                    {name: '江苏', value: 2675},
                    {name: '浙江', value: 2450},
                    {name: '四川', value: 2100},
                    {name: '湖北', value: 1980},
                    {name: '山东', value: 1875},
                    {name: '福建', value: 1760},
                    {name: '河南', value: 1650}
                ]
            };

            stats.value = mockData.stats;

            // 学历就业率柱状图
            const educationChart = echarts.init(document.getElementById('education-chart'));
            educationChart.setOption({
                backgroundColor: 'transparent',
                tooltip: {
                    trigger: 'axis',
                    backgroundColor: 'rgba(16, 22, 36, 0.9)',
                    borderColor: 'var(--primary)',
                    textStyle: {
                        color: '#fff'
                    }
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis: {
                    type: 'category',
                    data: mockData.educationLevels.map(e => e.level),
                    axisLine: {
                        lineStyle: {
                            color: 'rgba(160, 176, 192, 0.3)'
                        }
                    },
                    axisLabel: {
                        color: 'var(--text-secondary)'
                    }
                },
                yAxis: {
                    type: 'value',
                    name: '就业率 (%)',
                    nameTextStyle: {
                        color: 'var(--text-secondary)'
                    },
                    axisLine: {
                        lineStyle: {
                            color: 'rgba(160, 176, 192, 0.3)'
                        }
                    },
                    axisLabel: {
                        color: 'var(--text-secondary)'
                    },
                    splitLine: {
                        lineStyle: {
                            color: 'rgba(160, 176, 192, 0.1)'
                        }
                    }
                },
                series: [{
                    name: '就业率',
                    type: 'bar',
                    data: mockData.educationLevels.map(e => e.rate),
                    itemStyle: {
                        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                            { offset: 0, color: '#00f0ff' },
                            { offset: 1, color: '#0066ff' }
                        ])
                    }
                }]
            });

            // 行业占比饼图
            const industryChart = echarts.init(document.getElementById('industry-chart'));
            industryChart.setOption({
                backgroundColor: 'transparent',
                tooltip: {
                    trigger: 'item',
                    backgroundColor: 'rgba(16, 22, 36, 0.9)',
                    borderColor: 'var(--primary)',
                    textStyle: {
                        color: '#fff'
                    }
                },
                legend: {
                    orient: 'vertical',
                    right: 10,
                    top: 'center',
                    textStyle: {
                        color: 'var(--text-secondary)'
                    }
                },
                series: [{
                    name: '行业分布',
                    type: 'pie',
                    radius: ['40%', '70%'],
                    center: ['40%', '50%'],
                    itemStyle: {
                        borderRadius: 10,
                        borderColor: 'var(--dark-bg)',
                        borderWidth: 2
                    },
                    label: {
                        color: 'var(--text-secondary)'
                    },
                    emphasis: {
                        itemStyle: {
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    },
                    data: mockData.industryDistribution,
                    color: ['#00f0ff', '#bd00ff', '#00ffa3', '#ffcc00', '#ff4d6d', '#a0b0c0']
                }]
            });

            // 全国就业热力图
            const mapChart = echarts.init(document.getElementById('map-chart'));
            mapChart.setOption({
                backgroundColor: 'transparent',
                tooltip: {
                    trigger: 'item',
                    formatter: '{b}：{c}人',
                    backgroundColor: 'rgba(16, 22, 36, 0.9)',
                    borderColor: 'var(--primary)',
                    textStyle: {
                        color: '#fff'
                    }
                },
                visualMap: {
                    min: Math.min(...mockData.mapData.map(item => item.value)),
                    max: Math.max(...mockData.mapData.map(item => item.value)),
                    text: ['高', '低'],
                    realtime: false,
                    calculable: true,
                    inRange: {
                        color: ['#4575b4', '#74add1', '#abd9e9', '#e0f3f8', '#ffffbf', '#fee090', '#fdae61', '#f46d43', '#d73027']
                    },
                    textStyle: {
                        color: 'var(--text-secondary)'
                    }
                },
                series: [{
                    name: '就业人数',
                    type: 'map',
                    map: 'china',
                    roam: true,
                    label: {
                        show: true,
                        color: 'var(--text-primary)'
                    },
                    emphasis: {
                        label: {
                            color: '#fff'
                        },
                        itemStyle: {
                            areaColor: '#00c6ff'
                        }
                    },
                    data: mockData.mapData
                }]
            });

            // 响应式调整
            window.addEventListener('resize', function() {
                educationChart.resize();
                industryChart.resize();
                mapChart.resize();
            });
        };

        // 页面导航
        const navigate = (page) => {
            window.location.href = `../${page}/index.html`;
        };

        onMounted(() => {
            // 创建网格背景
            const gridBg = document.createElement('div');
            gridBg.className = 'grid-bg';
            document.body.appendChild(gridBg);

            createParticleFlow();
            initCharts();
        });

        return {
            stats,
            navigate
        };
    }
}).mount('#app');