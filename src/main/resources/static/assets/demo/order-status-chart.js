(async function () {
    const loggedInUser = await fetchLoggedInUser();
    if (!loggedInUser) {
        alert('로그인된 사용자 정보를 가져올 수 없습니다.');
        return;
    }

    const { team, dept } = loggedInUser;

    const now = new Date();
    let selectedYear = now.getFullYear();

    const buildApiUrl = (startDate, endDate) => {
        let apiUrl = '/api/ordersData';
        if (team) {
            apiUrl += `?team=${team}`;
        } else if (dept) {
            apiUrl += `?department=${dept}`;
        } else {
            console.error("No team or department available.");
            return null;
        }
        apiUrl += `&startDate=${startDate}&endDate=${endDate}`;
        return apiUrl;
    };

    const generateColors = (count) => {
        const baseColors = [
            'rgba(255, 99, 132, 0.6)', 'rgba(54, 162, 235, 0.6)', 'rgba(255, 206, 86, 0.6)',
            'rgba(75, 192, 192, 0.6)', 'rgba(153, 102, 255, 0.6)', 'rgba(255, 159, 64, 0.6)'
        ];

        const colors = [...baseColors];

        // 필요한 색상 수를 충족하도록 랜덤 색상 추가
        while (colors.length < count) {
            const r = Math.floor(Math.random() * 255);
            const g = Math.floor(Math.random() * 255);
            const b = Math.floor(Math.random() * 255);
            const alpha = 0.6;
            colors.push(`rgba(${r}, ${g}, ${b}, ${alpha})`);
        }

        return colors.slice(0, count);
    };

    let pieChartInstance = null;

    const updateYearlyChart = async (year) => {
        const startDate = `${year}-01-01`;
        const endDate = `${year}-12-31`;
        const apiUrl = buildApiUrl(startDate, endDate);

        if (!apiUrl) {
            alert('API URL 생성 실패. 팀 또는 부서 ID 확인.');
            return;
        }

        try {
            const response = await fetch(apiUrl);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const data = await response.json();

            if (!Array.isArray(data) || data.length === 0) {
                data = [{ employeeName: 'No Data', completedOrders: 0 }];
            }

            const labels = data.map(order => order.employeeName);
            const completedOrders = data.map(order => order.completedOrders);
            const colors = generateColors(labels.length);


            const pieCtx = document.getElementById('OrderStatusChart').getContext('2d');
            if (pieChartInstance) {
                pieChartInstance.destroy();
            }
            // 플러그인: 도넛 차트 중앙에 텍스트 추가
            Chart.pluginService.register({
                beforeDraw: function (chart) {
                    if (chart.config.type === 'doughnut') {
                        const width = chart.chart.width;
                        const height = chart.chart.height;
                        const ctx = chart.chart.ctx;

                        ctx.restore();
                        const fontSize = (height / 150).toFixed(2); // 폰트 크기 조절
                        ctx.font = `${fontSize}em sans-serif`;
                        ctx.textBaseline = 'middle';

                        // 총 완료 오더 수 계산
                        const total = chart.config.data.datasets[0].data.reduce((a, b) => a + b, 0);

                        const text = `총 ${total}건`; // 중앙 텍스트
                        const textX = Math.round((width - ctx.measureText(text).width) / 2);
                        const textY = height / 2;

                        ctx.fillText(text, textX, textY); // 텍스트 그리기
                        ctx.save();
                    }
                }
            });
            // 도넛 차트 생성
            pieChartInstance = new Chart(pieCtx, {
                type: 'doughnut',
                data: {
                    labels: labels,
                    datasets: [{
                        data: completedOrders,
                        backgroundColor: colors,
                        borderWidth: 1,
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: true,
                    cutoutPercentage: 50, // 도넛 중앙 공간 비율
                    legend: {
                        display: false // 범례 숨기기
                    },
                    tooltips: {
                        callbacks: {
                            label: function (tooltipItem, data) {
                                const dataIndex = tooltipItem.index;
                                const label = data.labels[dataIndex];
                                const value = data.datasets[0].data[dataIndex];
                                const total = data.datasets[0].data.reduce((a, b) => a + b, 0);
                                const percentage = ((value / total) * 100).toFixed(2);

                                // 툴팁 데이터 (줄바꿈 수정)
                                return `이름: ${label}\n완료 오더 수: ${value}건\n퍼센티지: ${percentage}%`.split('\n');
                            }
                        },
                        backgroundColor: 'rgba(0, 0, 0, 0.8)', // 툴팁 배경색
                        titleFontColor: '#fff', // 제목 색상
                        bodyFontColor: '#fff', // 본문 색상
                        borderWidth: 1, // 툴팁 테두리 두께
                        borderColor: '#ddd', // 툴팁 테두리 색상
                    }
                }
            });
        } catch (error) {
            console.error("Error fetching yearly data:", error);
        }
    };

    const changeYear = (direction) => {
        selectedYear += direction;
        const selectedYearSpan = document.getElementById('selectedYear'); // 연도 텍스트 요소 가져오기
        if (selectedYearSpan) {
            selectedYearSpan.textContent = selectedYear; // 연도 텍스트 업데이트
        }
        updateYearlyChart(selectedYear);
    };

    const yearlyHeader = document.getElementById('yearlySalesHeader');
    const yearlyControlDiv = document.createElement('div');
    yearlyControlDiv.innerHTML = `
        <button id="prevYearBtn">&lt;</button>
        <span id="selectedYear">${selectedYear}</span>년
        <button id="nextYearBtn">&gt;</button>
    `;
    yearlyHeader.appendChild(yearlyControlDiv);

    document.getElementById('prevYearBtn').addEventListener('click', () => changeYear(-1));
    document.getElementById('nextYearBtn').addEventListener('click', () => changeYear(1));

    // 초기 차트 로드
    updateYearlyChart(selectedYear);
})();
