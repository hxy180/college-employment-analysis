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

        // 时间范围
        const timeRange = ref('5y');

        // 日期范围
        const dateRange = ref([new Date(2018, 0, 1), new Date(2023, 11, 31)]);

        // 就业率类型
        const employmentType = ref('all');

        // 薪资趋势类型
        const salaryTrendType = ref('avg');

        // 预测类型
        const forecastType = ref('employment');

        // 行业选择
        const selectedIndustries = ref(['信息技术', '金融服务', '医疗健康']);
        const showLegend = ref(true);

        // 趋势数据
        const trendData = reactive({
            years: [2018, 2019, 2020, 2021, 2022, 2023],
            quarters: ['2018Q1', '2018Q2', '2018Q3', '2018Q4', '2019Q1', '2019Q2', '2019Q3', '2019Q4',
                '2020Q1', '2020Q2', '2020Q3', '2020Q4', '2021Q1', '2021Q2', '2021Q3', '2021Q4',
                '2022Q1', '2022Q2', '2022Q3', '2022Q4', '2023Q1', '2023Q2', '2023Q3'],
            employmentRates: {
                all: [89.2, 90.1, 85.3, 87.6, 88.9, 91.5],
                grad: [82.5, 83.7, 78.2, 80.4, 83.1, 85.7]
            },
            salaries: {
                avg: [7200, 7800, 8100, 8600, 9000, 9450],
                median: [6800, 7300, 7600, 8100, 8500, 8900]
            },
            industryEmployment: {
                '信息技术': [15200, 16800, 18500, 21000, 23500, 26500, 28500, 31000, 32000, 34500, 36500, 39000,
                    42000, 45000, 48000, 51000, 54500, 58000, 62000, 66000, 70000, 75000, 80000],
                '金融服务': [14200, 15000, 15500, 15800, 16200, 16800, 17200, 17800, 16500, 17000, 17500, 18200,
                    19000, 19800, 20500, 21500, 22500, 23500, 24500, 25500, 26500, 27500, 28500],
                '医疗健康': [9800, 10200, 10800, 11200, 11800, 12500, 13000, 13500, 14200, 15000, 15500, 16200,
                    16800, 17500, 18200, 19000, 19800, 20500, 21500, 22500, 23500, 24500, 25500],
                '教育科研': [12500, 12800, 13000, 13200, 13500, 13800, 14200, 14500, 13200, 13500, 13800, 14200,
                    14800, 15200, 15500, 15800, 16200, 16500, 16800, 17200, 17500, 17800, 18200],
                '制造业': [18500, 18800, 17500, 16800, 16200, 15800, 15500, 15200, 14200, 14500, 14800, 15200,
                    15800, 16200, 16500, 16800, 17200, 17500, 17800, 18200, 18500, 18800, 19200],
                '物流运输': [11500, 11800, 10500, 9800, 9500, 9200, 9000, 8800, 9200, 9500, 9800, 10200,
                    10500, 10800, 11200, 11500, 11800, 12200, 12500, 12800, 13200, 13500, 13800]
            }
        });

        // 所有行业
        const allIndustries = computed(() => Object.keys(trendData.industryEmployment));

        // 当前就业率
        const currentEmploymentRate = computed(() => {
            const rates = trendData.employmentRates[employmentType.value];
            return rates[rates.length - 1];
        });

        // 就业增长率
        const employmentGrowth = computed(() => {
            const rates = trendData.employmentRates[employmentType.value];
            return ((rates[rates.length - 1] - rates[rates.length - 2]) / rates[rates.length - 2] * 100).toFixed(1);
        });

        // 五年就业率变化
        const fiveYearEmploymentChange = computed(() => {
            const rates = trendData.employmentRates[employmentType.value];
            return ((rates[rates.length - 1] - rates[0]) / rates[0] * 100).toFixed(1);
        });

        // 当前薪资
        const currentSalary = computed(() => {
            const salaries = trendData.salaries[salaryTrendType.value];
            return salaries[salaries.length - 1];
        });

        // 薪资增长率
        const salaryGrowth = computed(() => {
            const salaries = trendData.salaries[salaryTrendType.value];
            return ((salaries[salaries.length - 1] - salaries[salaries.length - 2]) / salaries[salaries.length - 2] * 100).toFixed(1);
        });

        // 五年薪资增长
        const fiveYearSalaryGrowth = computed(() => {
            const salaries = trendData.salaries[salaryTrendType.value];
            return ((salaries[salaries.length - 1] - salaries[0]) / salaries[0] * 100).toFixed(1);
        });

        // 预测数据
        const forecastData = reactive({
            employment: {
                x: [2020, 2021, 2022, 2023, 2024, 2025],
                y: [85.3, 87.6, 88.9, 91.5, 93.2, 94.5],
                forecast: [91.5, 93.2, 94.5]
            },
            salary: {
                x: [2020, 2021, 2022, 2023, 2024, 2025],
                y: [8100, 8600, 9000, 9450, 9950, 10450],
                forecast: [9450, 9950, 10450]
            }
        });

        // 预测摘要
        const forecastSummary = computed(() => {
            if (forecastType.value === 'employment') {
                return "根据历史数据和当前经济形势预测，未来两年就业率将持续增长，2025年有望达到94.5%。信息技术和医疗健康行业将是主要增长点，传统行业就业率将保持稳定。";
            } else {
                return "预计未来两年薪资水平将保持6%-8%的年增长率，2025年平均薪资将突破10,000元。高技能人才薪资增长将高于平均水平，应届生起薪也将稳步提升。";
            }
        });

        // 预测置信度
        const forecastConfidence = computed(() => {
            return forecastType.value === 'employment' ? 85 : 78;
        });

        // 图例显示控制
        const toggleLegend = () => {
            showLegend.value = !showLegend.value;
            updateIndustryTrendChart();
        };

        // 获取置信度颜色
        const getConfidenceColor = (percentage) => {
            if (percentage >= 80) return '#00ffa3';
            if (percentage >= 60) return '#00f0ff';
            if (percentage >= 40) return '#ffcc00';
            return '#ff4d6d';
        };

        // 初始化图表
        const initCharts = () => {
            // 就业率趋势图
            const employmentTrendChart = echarts.init(document.getElementById('employment-trend-chart'));

            // 薪资趋势图
            const salaryTrendChart = echarts.init(document.getElementById('salary-trend-chart'));

            // 行业趋势图
            const industryTrendChart = echarts.init(document.getElementById('industry-trend-chart'));

            // 预测图
            const forecastChart = echarts.init(document.getElementById('forecast-chart'));

            // 更新就业率趋势图函数
            const updateEmploymentTrendChart = () => {
                const data = trendData.employmentRates[employmentType.value];

                employmentTrendChart.setOption({
                    backgroundColor: 'transparent',
                    tooltip: {
                        trigger: 'axis',
                        formatter: '{b0}: {c}%',
                        backgroundColor: 'rgba(16, 22, 36, 0.9)',
                        borderColor: 'var(--primary)',
                        textStyle: {
                            color: '#fff'
                        }
                    },
                    grid: {
                        left: '3%',
                        right: '4%',
                        bottom: '15%',
                        containLabel: true
                    },
                    xAxis: {
                        type: 'category',
                        data: trendData.years,
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
                        min: 75,
                        max: 100,
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
                            formatter: '{value}%'
                        },
                        splitLine: {
                            lineStyle: {
                                color: 'rgba(160, 176, 192, 0.1)'
                            }
                        }
                    },
                    series: [{
                        name: '就业率',
                        type: 'line',
                        data: data,
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
                        areaStyle: {
                            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                { offset: 0, color: 'rgba(189, 0, 255, 0.5)' },
                                { offset: 1, color: 'rgba(189, 0, 255, 0.1)' }
                            ])
                        },
                        markPoint: {
                            data: [
                                { type: 'max', name: '最高值' },
                                { type: 'min', name: '最低值' }
                            ]
                        }
                    }]
                });
            };

            // 初始化就业率趋势图
            updateEmploymentTrendChart();

            // 监听就业率类型变化
            watch(employmentType, updateEmploymentTrendChart);

            // 更新薪资趋势图函数
            const updateSalaryTrendChart = () => {
                const data = trendData.salaries[salaryTrendType.value];
                const isAvg = salaryTrendType.value === 'avg';

                salaryTrendChart.setOption({
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
                        bottom: '15%',
                        containLabel: true
                    },
                    xAxis: {
                        type: 'category',
                        data: trendData.years,
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
                        type: 'line',
                        data: data,
                        smooth: true,
                        symbol: 'circle',
                        symbolSize: 8,
                        lineStyle: {
                            width: 3,
                            color: '#00f0ff'
                        },
                        itemStyle: {
                            color: '#00f0ff',
                            borderColor: '#00f0ff',
                            borderWidth: 2
                        },
                        markLine: {
                            data: [
                                { type: 'average', name: '平均值' }
                            ],
                            lineStyle: {
                                color: '#ffcc00',
                                type: 'dashed'
                            }
                        }
                    }]
                });
            };

            // 初始化薪资趋势图
            updateSalaryTrendChart();

            // 监听薪资类型变化
            watch(salaryTrendType, updateSalaryTrendChart);

            // 更新行业趋势图函数
            const updateIndustryTrendChart = () => {
                const industries = selectedIndustries.value;
                const series = [];

                industries.forEach(industry => {
                    series.push({
                        name: industry,
                        type: 'line',
                        stack: 'Total',
                        areaStyle: {},
                        emphasis: {
                            focus: 'series'
                        },
                        data: trendData.industryEmployment[industry]
                    });
                });

                industryTrendChart.setOption({
                    backgroundColor: 'transparent',
                    tooltip: {
                        trigger: 'axis',
                        backgroundColor: 'rgba(16, 22, 36, 0.9)',
                        borderColor: 'var(--primary)',
                        textStyle: {
                            color: '#fff'
                        }
                    },
                    legend: {
                        data: industries,
                        show: showLegend.value,
                        textStyle: {
                            color: 'var(--text-secondary)'
                        },
                        right: 10,
                        top: 10
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
                        data: trendData.quarters,
                        axisLine: {
                            lineStyle: {
                                color: 'rgba(160, 176, 192, 0.3)'
                            }
                        },
                        axisLabel: {
                            color: 'var(--text-secondary)',
                            interval: 3,
                            rotate: 30
                        }
                    },
                    yAxis: {
                        type: 'value',
                        name: '就业人数',
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
                    series: series
                });
            };

            // 初始化行业趋势图
            updateIndustryTrendChart();

            // 监听行业选择变化
            watch(selectedIndustries, updateIndustryTrendChart);

            // 更新预测图函数
            const updateForecastChart = () => {
                const data = forecastData[forecastType.value];
                const isEmployment = forecastType.value === 'employment';

                forecastChart.setOption({
                    backgroundColor: 'transparent',
                    tooltip: {
                        trigger: 'axis',
                        formatter: isEmployment ? '{b0}: {c}%' : '¥{c}',
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
                        data: data.x,
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
                        name: isEmployment ? '就业率 (%)' : '薪资 (¥)',
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
                            formatter: isEmployment ? '{value}%' : '¥{value}'
                        },
                        splitLine: {
                            lineStyle: {
                                color: 'rgba(160, 176, 192, 0.1)'
                            }
                        }
                    },
                    series: [{
                        name: '历史数据',
                        type: 'line',
                        data: data.y.slice(0, 4),
                        smooth: true,
                        symbol: 'circle',
                        symbolSize: 8,
                        lineStyle: {
                            width: 3,
                            color: '#00f0ff'
                        },
                        itemStyle: {
                            color: '#00f0ff',
                            borderColor: '#00f0ff',
                            borderWidth: 2
                        }
                    }, {
                        name: '预测数据',
                        type: 'line',
                        data: [data.y[3], ...data.forecast],
                        smooth: true,
                        symbol: 'circle',
                        symbolSize: 8,
                        lineStyle: {
                            width: 3,
                            type: 'dashed',
                            color: '#bd00ff'
                        },
                        itemStyle: {
                            color: '#bd00ff',
                            borderColor: '#bd00ff',
                            borderWidth: 2
                        },
                        markArea: {
                            itemStyle: {
                                color: 'rgba(189, 0, 255, 0.1)'
                            },
                            data: [[
                                {
                                    name: '预测区间',
                                    xAxis: '2023'
                                },
                                {
                                    xAxis: '2025'
                                }
                            ]]
                        }
                    }]
                });
            };

            // 初始化预测图
            updateForecastChart();

            // 监听预测类型变化
            watch(forecastType, updateForecastChart);

            // 响应式调整
            window.addEventListener('resize', function() {
                employmentTrendChart.resize();
                salaryTrendChart.resize();
                industryTrendChart.resize();
                forecastChart.resize();
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
            timeRange,
            dateRange,
            employmentType,
            salaryTrendType,
            forecastType,
            selectedIndustries,
            showLegend,
            allIndustries,
            currentEmploymentRate,
            employmentGrowth,
            fiveYearEmploymentChange,
            currentSalary,
            salaryGrowth,
            fiveYearSalaryGrowth,
            forecastSummary,
            forecastConfidence,
            toggleLegend,
            getConfidenceColor
        };
    }
}).mount('#app');