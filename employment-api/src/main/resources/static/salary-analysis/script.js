const { createApp, ref, reactive, computed, onMounted, watch } = Vue;

createApp({
    setup() {
        // 页面导航
        const navigate = (page) => {
            window.location.href = `../${page}/index.html`;
        };

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

        // 薪资区间类型
        const salaryRangeType = ref('all');

        // 学历薪资类型
        const educationSalaryType = ref('avg');

        // 专业薪资类型
        const majorSalaryType = ref('top');

        // 行业薪资类型
        const industrySalaryType = ref('avg');

        // 薪资数据
        const salaryData = reactive({
            salaryRanges: {
                all: [
                    { range: '<5K', value: 15.2, color: '#ff4d6d' },
                    { range: '5K-8K', value: 28.5, color: '#ffcc00' },
                    { range: '8K-12K', value: 32.1, color: '#00f0ff' },
                    { range: '12K-15K', value: 16.3, color: '#bd00ff' },
                    { range: '>15K', value: 7.9, color: '#00ffa3' }
                ],
                grad: [
                    { range: '<5K', value: 32.4, color: '#ff4d6d' },
                    { range: '5K-8K', value: 42.1, color: '#ffcc00' },
                    { range: '8K-12K', value: 18.7, color: '#00f0ff' },
                    { range: '12K-15K', value: 5.3, color: '#bd00ff' },
                    { range: '>15K', value: 1.5, color: '#00ffa3' }
                ]
            },
            educationSalaries: {
                avg: [
                    { education: '博士', salary: 18500 },
                    { education: '硕士', salary: 12500 },
                    { education: '本科', salary: 8450 },
                    { education: '专科', salary: 6200 }
                ],
                median: [
                    { education: '博士', salary: 16800 },
                    { education: '硕士', salary: 11500 },
                    { education: '本科', salary: 7800 },
                    { education: '专科', salary: 5800 }
                ]
            },
            majorSalaries: [
                { major: '人工智能', salary: 18200 },
                { major: '数据科学', salary: 16800 },
                { major: '金融科技', salary: 15200 },
                { major: '软件工程', salary: 14200 },
                { major: '信息安全', salary: 13800 },
                { major: '电子工程', salary: 12800 },
                { major: '智能制造', salary: 11800 },
                { major: '生物医学', salary: 11200 },
                { major: '新能源', salary: 10800 },
                { major: '物联网工程', salary: 10200 },
                { major: '法学', salary: 8200 },
                { major: '工商管理', salary: 7800 },
                { major: '英语', salary: 7500 },
                { major: '市场营销', salary: 7200 },
                { major: '环境工程', salary: 6800 },
                { major: '教育学', salary: 6500 },
                { major: '汉语言文学', salary: 6200 },
                { major: '心理学', salary: 6000 },
                { major: '国际贸易', salary: 5800 },
                { major: '材料科学', salary: 5500 }
            ],
            industrySalaries: [
                {
                    industry: '信息技术',
                    avgSalary: 15200,
                    medianSalary: 13800,
                    minSalary: 8500,
                    maxSalary: 35000
                },
                {
                    industry: '金融服务',
                    avgSalary: 14500,
                    medianSalary: 13200,
                    minSalary: 8000,
                    maxSalary: 30000
                },
                {
                    industry: '科研教育',
                    avgSalary: 9800,
                    medianSalary: 9200,
                    minSalary: 6000,
                    maxSalary: 22000
                },
                {
                    industry: '医疗健康',
                    avgSalary: 11200,
                    medianSalary: 10500,
                    minSalary: 6500,
                    maxSalary: 28000
                },
                {
                    industry: '制造业',
                    avgSalary: 8800,
                    medianSalary: 8200,
                    minSalary: 5500,
                    maxSalary: 20000
                },
                {
                    industry: '文化传媒',
                    avgSalary: 9200,
                    medianSalary: 8600,
                    minSalary: 5800,
                    maxSalary: 25000
                },
                {
                    industry: '建筑房地产',
                    avgSalary: 10200,
                    medianSalary: 9500,
                    minSalary: 6000,
                    maxSalary: 28000
                },
                {
                    industry: '物流运输',
                    avgSalary: 7800,
                    medianSalary: 7200,
                    minSalary: 5000,
                    maxSalary: 18000
                },
                {
                    industry: '政府机构',
                    avgSalary: 8500,
                    medianSalary: 8000,
                    minSalary: 5500,
                    maxSalary: 15000
                },
                {
                    industry: '零售业',
                    avgSalary: 6800,
                    medianSalary: 6500,
                    minSalary: 4500,
                    maxSalary: 12000
                }
            ]
        });

        // 薪资摘要
        const salarySummary = ref({
            avgSalary: 8450,
            medianSalary: 7800,
            salaryGrowth: 6.7,
            topIndustry: '信息技术',
            topIndustrySalary: 15200,
            highSalaryPercent: 7.9
        });

        // 初始化图表
        const initCharts = () => {
            // 薪资区间分布图
            const salaryRangeChart = echarts.init(document.getElementById('salary-range-chart'));

            // 学历薪资对比图
            const educationSalaryChart = echarts.init(document.getElementById('education-salary-chart'));

            // 专业薪资对比图
            const majorSalaryChart = echarts.init(document.getElementById('major-salary-chart'));

            // 行业薪资对比图
            const industrySalaryChart = echarts.init(document.getElementById('industry-salary-chart'));

            // 更新薪资区间分布图函数
            const updateSalaryRangeChart = () => {
                const data = salaryData.salaryRanges[salaryRangeType.value];

                salaryRangeChart.setOption({
                    backgroundColor: 'transparent',
                    tooltip: {
                        trigger: 'item',
                        formatter: '{b}: {c}%',
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
                        name: '薪资分布',
                        type: 'pie',
                        radius: ['40%', '70%'],
                        center: ['40%', '50%'],
                        itemStyle: {
                            borderRadius: 10,
                            borderColor: 'var(--dark-bg)',
                            borderWidth: 2
                        },
                        label: {
                            color: 'var(--text-secondary)',
                            formatter: '{b}: {d}%'
                        },
                        emphasis: {
                            itemStyle: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: 'rgba(0, 0, 0, 0.5)'
                            }
                        },
                        data: data.map(item => ({
                            value: item.value,
                            name: item.range,
                            itemStyle: { color: item.color }
                        }))
                    }]
                });
            };

            // 初始化薪资区间分布图
            updateSalaryRangeChart();

            // 监听薪资区间类型变化
            watch(salaryRangeType, updateSalaryRangeChart);

            // 更新学历薪资对比图函数
            const updateEducationSalaryChart = () => {
                const data = salaryData.educationSalaries[educationSalaryType.value];
                const isAvg = educationSalaryType.value === 'avg';

                educationSalaryChart.setOption({
                    backgroundColor: 'transparent',
                    tooltip: {
                        trigger: 'axis',
                        formatter: '¥{c}',
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
                        data: data.map(item => item.education),
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
                        name: '薪资 (¥)',
                        nameTextStyle: {
                            color: 'var(--text-secondary)'
                        },
                        axisLine: {
                            lineStyle: {
                                color: 'rgba(160, 176, 192, 0.3)'
                            }
                        },
                        axisLabel: {
                            color: 'var(--text-secondary)',
                            formatter: '¥{value}'
                        },
                        splitLine: {
                            lineStyle: {
                                color: 'rgba(160, 176, 192, 0.1)'
                            }
                        }
                    },
                    series: [{
                        name: isAvg ? '平均薪资' : '薪资中位数',
                        type: 'bar',
                        data: data.map(item => item.salary),
                        itemStyle: {
                            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                { offset: 0, color: '#00f0ff' },
                                { offset: 1, color: '#0066ff' }
                            ])
                        },
                        label: {
                            show: true,
                            position: 'top',
                            formatter: '¥{c}',
                            color: 'var(--text-primary)'
                        }
                    }]
                });
            };

            // 初始化学历薪资对比图
            updateEducationSalaryChart();

            // 监听学历薪资类型变化
            watch(educationSalaryType, updateEducationSalaryChart);

            // 更新专业薪资对比图函数
            const updateMajorSalaryChart = () => {
                let data;

                if (majorSalaryType.value === 'top') {
                    data = [...salaryData.majorSalaries].slice(0, 10);
                } else if (majorSalaryType.value === 'bottom') {
                    data = [...salaryData.majorSalaries].slice(-10).reverse();
                } else {
                    data = [...salaryData.majorSalaries];
                }

                majorSalaryChart.setOption({
                    backgroundColor: 'transparent',
                    tooltip: {
                        trigger: 'axis',
                        formatter: '{b}: ¥{c}',
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
                        data: data.map(item => item.major),
                        axisLine: {
                            lineStyle: {
                                color: 'rgba(160, 176, 192, 0.3)'
                            }
                        },
                        axisLabel: {
                            color: 'var(--text-secondary)',
                            interval: 0,
                            rotate: majorSalaryType.value === 'all' ? 45 : 0
                        }
                    },
                    yAxis: {
                        type: 'value',
                        name: '薪资 (¥)',
                        nameTextStyle: {
                            color: 'var(--text-secondary)'
                        },
                        axisLine: {
                            lineStyle: {
                                color: 'rgba(160, 176, 192, 0.3)'
                            }
                        },
                        axisLabel: {
                            color: 'var(--text-secondary)',
                            formatter: '¥{value}'
                        },
                        splitLine: {
                            lineStyle: {
                                color: 'rgba(160, 176, 192, 0.1)'
                            }
                        }
                    },
                    series: [{
                        name: '平均薪资',
                        type: majorSalaryType.value === 'all' ? 'line' : 'bar',
                        data: data.map(item => item.salary),
                        smooth: true,
                        symbol: 'circle',
                        symbolSize: 8,
                        lineStyle: {
                            width: 3,
                            color: '#bd00ff'
                        },
                        itemStyle: {
                            color: '#bd00ff',
                            borderColor: '#bd00ff',
                            borderWidth: 2
                        },
                        areaStyle: majorSalaryType.value === 'all' ? {
                            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                { offset: 0, color: 'rgba(189, 0, 255, 0.5)' },
                                { offset: 1, color: 'rgba(189, 0, 255, 0.1)' }
                            ])
                        } : undefined,
                        label: majorSalaryType.value === 'all' ? undefined : {
                            show: true,
                            position: 'top',
                            formatter: '¥{c}',
                            color: 'var(--text-primary)'
                        }
                    }]
                });
            };

            // 初始化专业薪资对比图
            updateMajorSalaryChart();

            // 监听专业薪资类型变化
            watch(majorSalaryType, updateMajorSalaryChart);

            // 更新行业薪资对比图函数
            const updateIndustrySalaryChart = () => {
                const isAvg = industrySalaryType.value === 'avg';
                const isMedian = industrySalaryType.value === 'median';
                const isRange = industrySalaryType.value === 'range';

                industrySalaryChart.setOption({
                    backgroundColor: 'transparent',
                    tooltip: {
                        trigger: 'axis',
                        backgroundColor: 'rgba(16, 22, 36, 0.9)',
                        borderColor: 'var(--primary)',
                        textStyle: {
                            color: '#fff'
                        },
                        formatter: function(params) {
                            if (isRange) {
                                const data = params[0].data;
                                return `${data.industry}<br/>
                                        最高: ¥${data.maxSalary.toLocaleString()}<br/>
                                        平均: ¥${data.avgSalary.toLocaleString()}<br/>
                                        最低: ¥${data.minSalary.toLocaleString()}`;
                            } else {
                                return `${params[0].name}: ¥${params[0].value.toLocaleString()}`;
                            }
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
                        data: salaryData.industrySalaries.map(item => item.industry),
                        axisLine: {
                            lineStyle: {
                                color: 'rgba(160, 176, 192, 0.3)'
                            }
                        },
                        axisLabel: {
                            color: 'var(--text-secondary)',
                            interval: 0,
                            rotate: 30
                        }
                    },
                    yAxis: {
                        type: 'value',
                        name: '薪资 (¥)',
                        nameTextStyle: {
                            color: 'var(--text-secondary)'
                        },
                        axisLine: {
                            lineStyle: {
                                color: 'rgba(160, 176, 192, 0.3)'
                            }
                        },
                        axisLabel: {
                            color: 'var(--text-secondary)',
                            formatter: '¥{value}'
                        },
                        splitLine: {
                            lineStyle: {
                                color: 'rgba(160, 176, 192, 0.1)'
                            }
                        }
                    },
                    series: isRange ? [
                        {
                            name: '薪资范围',
                            type: 'boxplot',
                            data: salaryData.industrySalaries.map(item => [
                                item.minSalary,
                                item.minSalary,
                                item.medianSalary,
                                item.avgSalary,
                                item.maxSalary
                            ]),
                            itemStyle: {
                                color: '#00f0ff',
                                borderColor: '#bd00ff'
                            },
                            tooltip: {
                                formatter: function(param) {
                                    const item = salaryData.industrySalaries[param.dataIndex];
                                    return `${item.industry}<br/>
                                            最高: ¥${item.maxSalary.toLocaleString()}<br/>
                                            平均: ¥${item.avgSalary.toLocaleString()}<br/>
                                            中位数: ¥${item.medianSalary.toLocaleString()}<br/>
                                            最低: ¥${item.minSalary.toLocaleString()}`;
                                }
                            }
                        }
                    ] : [
                        {
                            name: isAvg ? '平均薪资' : '薪资中位数',
                            type: 'bar',
                            data: salaryData.industrySalaries.map(item =>
                                isAvg ? item.avgSalary : item.medianSalary
                            ),
                            itemStyle: {
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                    { offset: 0, color: '#00f0ff' },
                                    { offset: 1, color: '#bd00ff' }
                                ])
                            },
                            label: {
                                show: true,
                                position: 'top',
                                formatter: '¥{c}',
                                color: 'var(--text-primary)'
                            }
                        }
                    ]
                });
            };

            // 初始化行业薪资对比图
            updateIndustrySalaryChart();

            // 监听行业薪资类型变化
            watch(industrySalaryType, updateIndustrySalaryChart);

            // 响应式调整
            window.addEventListener('resize', function() {
                salaryRangeChart.resize();
                educationSalaryChart.resize();
                majorSalaryChart.resize();
                industrySalaryChart.resize();
            });
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
            navigate,
            salaryRangeType,
            educationSalaryType,
            majorSalaryType,
            industrySalaryType,
            salarySummary
        };
    }
}).mount('#app');