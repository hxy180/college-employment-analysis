const { createApp, ref, reactive, computed, onMounted, watch } = Vue;
const { ElTable, ElTableColumn, ElPagination, ElInput, ElSelect, ElOption, ElProgress, ElTag } = ElementPlus;

createApp({
    components: {
        ElTable,
        ElTableColumn,
        ElPagination,
        ElInput,
        ElSelect,
        ElOption,
        ElProgress,
        ElTag
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

        // 地图类型
        const mapType = ref('employment');

        // 地区类型
        const regionType = ref('province');

        // 表格相关状态
        const search = ref('');
        const filterRegion = ref('all');
        const currentPage = ref(1);
        const pageSize = 10;
        const sortProp = ref('employment');
        const sortOrder = ref('descending');

        // 地区数据
        const regionData = reactive({
            provinces: [],
            cities: [],
            mapData: []
        });

        // 地区摘要数据
        const topEmployment = ref({ region: '北京', employment: 34520, growth: 12.5 });
        const topSalary = ref({ region: '上海', salary: 15200, growth: 8.7 });
        const topGrowth = ref({ region: '深圳', rate: 18.3, increase: 8920 });

        // 模拟省份数据
        const mockProvinces = [
            "北京", "上海", "广东", "江苏", "浙江",
            "四川", "湖北", "山东", "福建", "河南",
            "湖南", "安徽", "河北", "辽宁", "陕西",
            "重庆", "天津", "江西", "广西", "云南"
        ];

        // 模拟城市数据
        const mockCities = [
            "北京", "上海", "深圳", "广州", "杭州",
            "成都", "南京", "武汉", "苏州", "重庆",
            "天津", "西安", "长沙", "郑州", "青岛",
            "宁波", "合肥", "佛山", "东莞", "厦门"
        ];

        // 模拟行业数据
        const mockIndustries = [
            "信息技术", "金融服务", "教育科研", "医疗健康", "制造业",
            "文化传媒", "建筑房地产", "物流运输", "政府机构", "零售业"
        ];

        // 区域划分
        const regionGroups = {
            east: ["北京", "上海", "广东", "江苏", "浙江", "福建", "天津", "山东"],
            central: ["湖北", "湖南", "安徽", "江西", "河南", "山西"],
            west: ["四川", "重庆", "陕西", "广西", "云南", "贵州", "甘肃", "新疆"],
            northeast: ["辽宁", "吉林", "黑龙江"]
        };

        // 初始化数据
        const initData = () => {
            // 省份数据
            regionData.provinces = mockProvinces.map(province => {
                const employment = 10000 + Math.floor(Math.random() * 25000);
                const growth = 5 + Math.random() * 15;
                const salary = 8000 + Math.floor(Math.random() * 10000);
                const salaryGrowth = 3 + Math.random() * 8;

                // 确定区域
                let regionGroup = 'east';
                if (regionGroups.central.includes(province)) regionGroup = 'central';
                if (regionGroups.west.includes(province)) regionGroup = 'west';
                if (regionGroups.northeast.includes(province)) regionGroup = 'northeast';

                // 行业分布
                const industries = [...mockIndustries].sort(() => 0.5 - Math.random()).slice(0, 3);
                const totalPercent = 30 + Math.floor(Math.random() * 50);
                const industryDistribution = industries.map((industry, idx) => ({
                    name: industry,
                    percent: Math.floor(totalPercent * (1 - idx * 0.3))
                }));

                return {
                    region: province,
                    regionGroup,
                    employment,
                    growth: parseFloat(growth.toFixed(1)),
                    avgSalary: salary,
                    salaryGrowth: parseFloat(salaryGrowth.toFixed(1)),
                    topIndustries: industryDistribution,
                    graduateRatio: 30 + Math.floor(Math.random() * 60)
                };
            });

            // 城市数据
            regionData.cities = mockCities.map(city => {
                const employment = 5000 + Math.floor(Math.random() * 15000);
                const growth = 5 + Math.random() * 20;
                const salary = 9000 + Math.floor(Math.random() * 12000);
                const salaryGrowth = 4 + Math.random() * 10;

                // 确定省份
                let province = "广东";
                if (city === "北京" || city === "天津") province = "北京/天津";
                else if (city === "上海") province = "上海";
                else if (["杭州", "宁波"].includes(city)) province = "浙江";
                else if (["南京", "苏州"].includes(city)) province = "江苏";

                // 行业分布
                const industries = [...mockIndustries].sort(() => 0.5 - Math.random()).slice(0, 3);
                const totalPercent = 40 + Math.floor(Math.random() * 40);
                const industryDistribution = industries.map((industry, idx) => ({
                    name: industry,
                    percent: Math.floor(totalPercent * (1 - idx * 0.3))
                }));

                return {
                    region: city,
                    province,
                    employment,
                    growth: parseFloat(growth.toFixed(1)),
                    avgSalary: salary,
                    salaryGrowth: parseFloat(salaryGrowth.toFixed(1)),
                    topIndustries: industryDistribution,
                    graduateRatio: 20 + Math.floor(Math.random() * 70)
                };
            });

            // 地图数据
            regionData.mapData = mockProvinces.map(province => ({
                name: province,
                value: 10000 + Math.floor(Math.random() * 25000),
                salary: 8000 + Math.floor(Math.random() * 10000)
            }));

            // 设置摘要数据
            const employmentSorted = [...regionData.provinces].sort((a, b) => b.employment - a.employment);
            const salarySorted = [...regionData.provinces].sort((a, b) => b.avgSalary - a.avgSalary);
            const growthSorted = [...regionData.provinces].sort((a, b) => b.growth - a.growth);

            topEmployment.value = {
                region: employmentSorted[0].region,
                employment: employmentSorted[0].employment,
                growth: employmentSorted[0].growth
            };

            topSalary.value = {
                region: salarySorted[0].region,
                salary: salarySorted[0].avgSalary,
                growth: salarySorted[0].salaryGrowth
            };

            topGrowth.value = {
                region: growthSorted[0].region,
                rate: growthSorted[0].growth,
                increase: Math.round(growthSorted[0].employment * growthSorted[0].growth / 100)
            };
        };

        // 初始化图表
        const initCharts = () => {
            // 地图图表
            const mapChart = echarts.init(document.getElementById('map-chart'));

            // 对比图表
            const comparisonChart = echarts.init(document.getElementById('comparison-chart'));

            // 更新地图函数
            const updateMapChart = () => {
                const isSalaryMap = mapType.value === 'salary';

                mapChart.setOption({
                    backgroundColor: 'transparent',
                    tooltip: {
                        trigger: 'item',
                        formatter: isSalaryMap ?
                            '{b}<br/>平均薪资: ¥{c}' :
                            '{b}<br/>就业人数: {c}人',
                        backgroundColor: 'rgba(16, 22, 36, 0.9)',
                        borderColor: 'var(--primary)',
                        textStyle: {
                            color: '#fff'
                        }
                    },
                    visualMap: {
                        min: isSalaryMap ?
                            Math.min(...regionData.mapData.map(d => d.salary)) :
                            Math.min(...regionData.mapData.map(d => d.value)),
                        max: isSalaryMap ?
                            Math.max(...regionData.mapData.map(d => d.salary)) :
                            Math.max(...regionData.mapData.map(d => d.value)),
                        text: ['高', '低'],
                        realtime: false,
                        calculable: true,
                        inRange: {
                            color: isSalaryMap ?
                                ['#4575b4', '#74add1', '#abd9e9', '#e0f3f8', '#ffffbf', '#fee090', '#fdae61', '#f46d43', '#d73027'] :
                                ['#e0f3f8', '#abd9e9', '#74add1', '#4575b4', '#313695']
                        },
                        textStyle: {
                            color: 'var(--text-secondary)'
                        }
                    },
                    series: [{
                        name: isSalaryMap ? '平均薪资' : '就业人数',
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
                        data: regionData.mapData.map(d => ({
                            name: d.name,
                            value: isSalaryMap ? d.salary : d.value
                        }))
                    }]
                });
            };

            // 初始化地图
            updateMapChart();

            // 监听地图类型变化
            watch(mapType, updateMapChart);

            // 更新对比图表函数
            const updateComparisonChart = () => {
                const data = regionType.value === 'province' ?
                    regionData.provinces.slice(0, 10) :
                    regionData.cities.slice(0, 10);

                comparisonChart.setOption({
                    backgroundColor: 'transparent',
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: {
                            type: 'shadow'
                        },
                        backgroundColor: 'rgba(16, 22, 36, 0.9)',
                        borderColor: 'var(--primary)',
                        textStyle: {
                            color: '#fff'
                        }
                    },
                    legend: {
                        data: ['就业人数', '平均薪资'],
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
                        data: data.map(d => d.region),
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
                    yAxis: [
                        {
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
                                color: 'var(--text-secondary)',
                                formatter: '{value}人'
                            },
                            splitLine: {
                                show: false
                            }
                        },
                        {
                            type: 'value',
                            name: '平均薪资 (¥)',
                            nameTextStyle: {
                                color: 'var(--text-secondary)'
                            },
                            min: 0,
                            max: regionType.value === 'province' ? 25000 : 30000,
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
                                show: false
                            }
                        }
                    ],
                    series: [
                        {
                            name: '就业人数',
                            type: 'bar',
                            data: data.map(d => d.employment),
                            itemStyle: {
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                    { offset: 0, color: '#00f0ff' },
                                    { offset: 1, color: '#0066ff' }
                                ])
                            }
                        },
                        {
                            name: '平均薪资',
                            type: 'line',
                            yAxisIndex: 1,
                            data: data.map(d => d.avgSalary),
                            symbol: 'circle',
                            symbolSize: 8,
                            lineStyle: {
                                width: 3,
                                color: '#ffcc00'
                            },
                            itemStyle: {
                                color: '#ffcc00',
                                borderColor: '#ffcc00',
                                borderWidth: 2
                            }
                        }
                    ]
                });
            };

            // 初始化对比图表
            updateComparisonChart();

            // 监听地区类型变化
            watch(regionType, updateComparisonChart);

            // 响应式调整
            window.addEventListener('resize', function() {
                mapChart.resize();
                comparisonChart.resize();
            });
        };

        // 表格数据筛选和排序
        const filteredTableData = computed(() => {
            let data = regionType.value === 'province' ?
                [...regionData.provinces] :
                [...regionData.cities];

            // 搜索过滤
            if (search.value) {
                const searchLower = search.value.toLowerCase();
                data = data.filter(item =>
                    item.region.toLowerCase().includes(searchLower)
                );
            }

            // 区域过滤
            if (filterRegion.value !== 'all') {
                data = data.filter(item =>
                    item.regionGroup === filterRegion.value
                );
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
            if (percentage >= 70) return '#00ffa3';
            if (percentage >= 50) return '#00f0ff';
            if (percentage >= 30) return '#ffcc00';
            return '#ff4d6d';
        };

        // 获取行业标签类型
        const getIndustryTagType = (industry) => {
            const industryColors = {
                "信息技术": "primary",
                "金融服务": "success",
                "教育科研": "warning",
                "医疗健康": "danger",
                "制造业": ""
            };
            return industryColors[industry.name] || "";
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
            mapType,
            regionType,
            search,
            filterRegion,
            currentPage,
            pageSize,
            topEmployment,
            topSalary,
            topGrowth,
            filteredTableData: pagedTableData,
            handleSortChange,
            getProgressColor,
            getIndustryTagType
        };
    }
}).mount('#app');