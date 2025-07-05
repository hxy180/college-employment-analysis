const { createApp, ref, reactive, computed, onMounted, watch } = Vue;
const { ElTable, ElTableColumn, ElPagination, ElInput, ElSelect, ElOption, ElProgress } = ElementPlus;

createApp({
    components: {
        ElTable,
        ElTableColumn,
        ElPagination,
        ElInput,
        ElSelect,
        ElOption,
        ElProgress
    },
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

        // 就业率选项卡
        const employmentTab = ref('top');

        // 表格相关状态
        const search = ref('');
        const filterField = ref('all');
        const currentPage = ref(1);
        const pageSize = 10;
        const sortProp = ref('employmentRate');
        const sortOrder = ref('descending');

        // 专业数据
        const majorData = reactive({
            employmentTop10: [],
            employmentBottom10: [],
            salaryData: [],
            sankeyData: {
                nodes: [],
                links: []
            },
            tableData: []
        });

        // 模拟数据
        const mockMajors = [
            "人工智能", "数据科学", "软件工程", "信息安全", "物联网工程",
            "金融科技", "生物医学", "电子工程", "智能制造", "新能源",
            "环境工程", "材料科学", "工商管理", "市场营销", "国际贸易",
            "汉语言文学", "英语", "法学", "心理学","计算机", "教育学"
        ];

        const mockIndustries = [
            "信息技术", "金融服务", "教育科研", "医疗健康", "制造业",
            "文化传媒", "建筑房地产", "物流运输", "政府机构", "零售业"
        ];

        // 初始化数据
        const initData = () => {
            // 就业率TOP10
            majorData.employmentTop10 = mockMajors.slice(0, 10).map((major, index) => ({
                major,
                employmentRate: 95 - index
            }));

            // 就业率BOTTOM10
            majorData.employmentBottom10 = mockMajors.slice(10, 20).map((major, index) => ({
                major,
                employmentRate: 75 - index
            }));

            // 薪资数据
            majorData.salaryData = mockMajors.map((major, index) => ({
                major,
                avgSalary: 8000 + Math.floor(Math.random() * 10000)
            })).sort((a, b) => b.avgSalary - a.avgSalary);

            // 桑基图数据
            const nodes = [
                ...mockMajors.map(major => ({ name: major })),
                ...mockIndustries.map(industry => ({ name: industry }))
            ];

            const links = [];
            mockMajors.forEach(major => {
                // 每个专业流向3-5个行业
                const industryCount = 3 + Math.floor(Math.random() * 3);
                const industries = [...mockIndustries].sort(() => 0.5 - Math.random()).slice(0, industryCount);

                industries.forEach(industry => {
                    const value = 500 + Math.floor(Math.random() * 1500);
                    links.push({
                        source: major,
                        target: industry,
                        value: value
                    });
                });
            });

            majorData.sankeyData = { nodes, links };

            // 表格数据
            majorData.tableData = mockMajors.map((major, index) => {
                const employmentRate = 75 + Math.floor(Math.random() * 20);
                const industries = [...mockIndustries].sort(() => 0.5 - Math.random()).slice(0, 3);

                return {
                    major,
                    employmentRate,
                    avgSalary: 8000 + Math.floor(Math.random() * 10000),
                    employmentCount: 150 + Math.floor(Math.random() * 350),
                    topIndustry: industries[0],
                    industryPercent: 30 + Math.floor(Math.random() * 50)
                };
            });
        };

        // 初始化图表
        const initCharts = () => {
            // 就业率图表
            const employmentChart = echarts.init(document.getElementById('employment-chart'));

            // 薪资图表
            const salaryChart = echarts.init(document.getElementById('salary-chart'));

            // 桑基图
            const sankeyChart = echarts.init(document.getElementById('sankey-chart'));

            // 更新就业率图表函数
            const updateEmploymentChart = () => {
                const data = employmentTab.value === 'top' ?
                    [...majorData.employmentTop10].reverse() :
                    majorData.employmentBottom10;

                employmentChart.setOption({
                    backgroundColor: 'transparent',
                    tooltip: {
                        trigger: 'axis',
                        backgroundColor: 'rgba(16, 22, 36, 0.9)',
                        borderColor: 'var(--primary)',
                        formatter: '{b}: {c}%',
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
                        type: 'value',
                        min: employmentTab.value === 'top' ? 85 : 65,
                        max: 100,
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
                    yAxis: {
                        type: 'category',
                        data: data.map(item => item.major),
                        axisLine: {
                            lineStyle: {
                                color: 'rgba(160, 176, 192, 0.3)'
                            }
                        },
                        axisLabel: {
                            color: 'var(--text-secondary)'
                        }
                    },
                    series: [{
                        name: '就业率',
                        type: 'bar',
                        data: data.map(item => item.employmentRate),
                        itemStyle: {
                            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                                { offset: 0, color: '#00f0ff' },
                                { offset: 1, color: '#bd00ff' }
                            ])
                        },
                        label: {
                            show: true,
                            position: 'right',
                            formatter: '{c}%',
                            color: 'var(--text-primary)'
                        }
                    }]
                });
            };

            // 初始化就业率图表
            updateEmploymentChart();

            // 监听就业率选项卡变化
            watch(employmentTab, updateEmploymentChart);

            // 设置薪资图表
            salaryChart.setOption({
                backgroundColor: 'transparent',
                tooltip: {
                    trigger: 'axis',
                    backgroundColor: 'rgba(16, 22, 36, 0.9)',
                    borderColor: 'var(--primary)',
                    formatter: '{b}: ¥{c}',
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
                    data: majorData.salaryData.slice(0, 10).map(item => item.major),
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
                    name: '平均薪资 (¥)',
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
                    type: 'bar',
                    data: majorData.salaryData.slice(0, 10).map(item => item.avgSalary),
                    itemStyle: {
                        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                            { offset: 0, color: '#00f0ff' },
                            { offset: 1, color: '#0066ff' }
                        ])
                    }
                }]
            });

            // 设置桑基图
            sankeyChart.setOption({
                backgroundColor: 'transparent',
                tooltip: {
                    trigger: 'item',
                    backgroundColor: 'rgba(16, 22, 36, 0.9)',
                    borderColor: 'var(--primary)',
                    textStyle: {
                        color: '#fff'
                    }
                },
                series: [{
                    type: 'sankey',
                    layout: 'none',
                    emphasis: {
                        focus: 'adjacency'
                    },
                    data: majorData.sankeyData.nodes,
                    links: majorData.sankeyData.links,
                    lineStyle: {
                        color: 'gradient',
                        curveness: 0.5,
                        opacity: 0.3
                    },
                    label: {
                        color: 'rgba(255, 255, 255, 0.7)',
                        fontSize: 12
                    },
                    levels: [{
                        depth: 0,
                        itemStyle: {
                            color: '#bd00ff'
                        },
                        lineStyle: {
                            color: 'source',
                            opacity: 0.6
                        }
                    }, {
                        depth: 1,
                        itemStyle: {
                            color: '#00f0ff'
                        }
                    }]
                }]
            });

            // 响应式调整
            window.addEventListener('resize', function() {
                employmentChart.resize();
                salaryChart.resize();
                sankeyChart.resize();
            });
        };

        // 表格数据筛选和排序
        const filteredTableData = computed(() => {
            let data = [...majorData.tableData];

            // 搜索过滤
            if (search.value) {
                const searchLower = search.value.toLowerCase();
                data = data.filter(item => {
                    if (filterField.value === 'all') {
                        return Object.values(item).some(
                            val => String(val).toLowerCase().includes(searchLower)
                        );
                    } else {
                        return String(item[filterField.value]).toLowerCase().includes(searchLower);
                    }
                });
            }

            // 排序
            if (sortProp.value) {
                data.sort((a, b) => {
                    const aValue = a[sortProp.value];
                    const bValue = b[sortProp.value];

                    if (sortOrder.value === 'ascending') {
                        return aValue - bValue;
                    } else {
                        return bValue - aValue;
                    }
                });
            }

            return data;
        });

        // 分页数据
        const pagedTableData = computed(() => {
            const start = (currentPage.value - 1) * pageSize;
            return filteredTableData.value.slice(start, start + pageSize);
        });

        // 排序处理
        const handleSortChange = ({ prop, order }) => {
            sortProp.value = prop;
            sortOrder.value = order || 'descending';
        };

        // 获取进度条颜色
        const getProgressColor = (percentage) => {
            if (percentage >= 90) return '#00ffa3';
            if (percentage >= 80) return '#00f0ff';
            if (percentage >= 70) return '#ffcc00';
            return '#ff4d6d';
        };

        onMounted(() => {
            // 创建网格背景
            const gridBg = document.createElement('div');
            gridBg.className = 'grid-bg';
            document.body.appendChild(gridBg);

            createParticleFlow();
            initData();
            initCharts();
        });

        return {
            navigate,
            employmentTab,
            search,
            filterField,
            currentPage,
            pageSize,
            filteredTableData: pagedTableData,
            handleSortChange,
            getProgressColor
        };
    }
}).mount('#app');