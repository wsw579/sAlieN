(async function () {
    // 로그인된 사용자 정보 가져오기
    const loggedInUser = await fetchLoggedInUser();
    if (!loggedInUser) {
        alert('로그인된 사용자 정보를 가져올 수 없습니다.');
        return;
    }

    const { team, dept } = loggedInUser;
    const now = new Date();
    let selectedYear = now.getFullYear();
    let selectedMonth = now.getMonth() + 1; // 1부터 시작하도록 수정

    const maxYear = now.getFullYear(); // 최대 연도 = 현재 연도
    const maxMonth = now.getMonth() + 1; // 최대 월 = 현재 월

    // 시작일과 종료일 계산
    const getFirstAndLastDay = (year, month) => {
        const firstDay = new Date(year, month - 1, 1).toISOString().split('T')[0];
        const lastDay = new Date(year, month, 0).toISOString().split('T')[0];
        return { firstDay, lastDay };
    };

    const { firstDay, lastDay } = getFirstAndLastDay(selectedYear, selectedMonth);

    // API URL 생성
    const buildApiUrl = (startDate, endDate) => {
        let apiUrl = '/api/ordersData';
        let params = [];

        if (team) {
            params.push(`team=${team}`);
        } else if (dept) {
            params.push(`department=${dept}`);
        } else {
            console.error("No team or department available.");
            return null;
        }

        params.push(`startDate=${startDate}`, `endDate=${endDate}`);
        apiUrl += '?' + params.join('&');

        return apiUrl;
    };

    let chartInstance = null;

    // 차트 업데이트 함수
    const updateChart = async (startDate, endDate) => {
        const dynamicApiUrl = buildApiUrl(startDate, endDate);
        if (!dynamicApiUrl) {
            alert('API URL 생성 실패. 팀 또는 부서 ID 확인.');
            return;
        }

        try {
            const response = await fetch(dynamicApiUrl);
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
            }
            let data = await response.json();

            if (!Array.isArray(data) || data.length === 0) {
                data = [{ employeeName: 'No Data', totalOrders: 0, completedOrders: 0 }];
            }

            const labels = data.map(order => order.employeeName || 'Unknown');
            const totalOrders = data.map(order => order.totalOrders || 0);
            const completedOrders = data.map(order => order.completedOrders || 0);

            const ctx = document.getElementById('employeeOrderChart').getContext('2d');
            if (chartInstance) {
                chartInstance.destroy();
            }

            // Chart.js 2.x 문법 유지
            chartInstance = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [
                        {
                            label: 'Completed Orders',
                            data: completedOrders,
                            backgroundColor: 'rgba(75, 192, 192, 0.6)',
                            borderColor: 'rgba(75, 192, 192, 1)',
                            borderWidth: 1,
                        },
                        {
                            label: 'Remaining Orders',
                            data: totalOrders.map((total, index) => total - completedOrders[index]),
                            backgroundColor: 'rgba(54, 162, 235, 0.6)',
                            borderColor: 'rgba(54, 162, 235, 1)',
                            borderWidth: 1,
                        },
                    ],
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: true,
                    scales: {
                        xAxes: [
                            {
                                stacked: true,
                                ticks: {
                                    autoSkip: true, // 레이블 자동 스킵
                                    maxRotation: 45, // 최대 회전 각도
                                    minRotation: 0, // 최소 회전 각도
                                },
                            },
                        ],
                        yAxes: [
                            {
                                stacked: true,
                                ticks: {
                                    beginAtZero: true,
                                },
                            },
                        ],
                    },
                    tooltips: {
                        callbacks: {
                            label: function (tooltipItem, data) {
                                const dataset = data.datasets[tooltipItem.datasetIndex];
                                const value = dataset.data[tooltipItem.index];
                                return `${dataset.label}: ${value}`;
                            },
                        },
                    },
                },
            });
        } catch (error) {
            console.error("Error fetching order data:", error);
        }
    };

    // 월 변경 버튼 추가
    const employeeOrderChartHeader = document.getElementById('employeeOrderChartHeader');
    if (!employeeOrderChartHeader) {
        console.error("employeeOrderChartHeader 요소를 찾을 수 없습니다.");
        return;
    }

    const monthControlDiv = document.createElement("div");
    monthControlDiv.classList.add("d-flex", "align-items-center", "mx-auto");
    monthControlDiv.innerHTML = `
        <button id="prevMonthBtn" class="btn btn-outline-secondary btn-sm">&lt;</button>
        <span id="selectedMonthYear" class="mx-2">${selectedYear}년 ${selectedMonth}월</span>
        <button id="nextMonthBtn" class="btn btn-outline-secondary btn-sm">&gt;</button>
    `;

    employeeOrderChartHeader.classList.add("d-flex", "align-items-center", "position-relative");
    employeeOrderChartHeader.appendChild(monthControlDiv);
    monthControlDiv.style.position = "absolute";
    monthControlDiv.style.left = "50%";
    monthControlDiv.style.transform = "translateX(-50%)";

    const prevMonthBtn = document.getElementById("prevMonthBtn");
    const nextMonthBtn = document.getElementById("nextMonthBtn");
    const selectedMonthYear = document.getElementById("selectedMonthYear");

    const minYear = 2020;

    function updateNavigation() {
        selectedMonthYear.textContent = `${selectedYear}년 ${selectedMonth}월`;

        prevMonthBtn.disabled = selectedYear === minYear && selectedMonth === 1;
        nextMonthBtn.disabled = selectedYear === maxYear && selectedMonth >= maxMonth;
    }

    function changeMonth(delta) {
        let newMonth = selectedMonth + delta;
        let newYear = selectedYear;

        if (newMonth < 1) {
            if (selectedYear > minYear) {
                newYear--;
                newMonth = 12;
            } else {
                return;
            }
        } else if (newMonth > 12) {
            if (selectedYear < maxYear) {
                newYear++;
                newMonth = 1;
            } else if (selectedYear === maxYear && newMonth > maxMonth) {
                return;
            }
        } else if (selectedYear === maxYear && newMonth > maxMonth) {
            return;
        }

        selectedYear = newYear;
        selectedMonth = newMonth;

        updateNavigation();
        updateChart(...Object.values(getFirstAndLastDay(selectedYear, selectedMonth)));
    }

    prevMonthBtn.addEventListener("click", () => changeMonth(-1));
    nextMonthBtn.addEventListener("click", () => changeMonth(1));

    updateChart(firstDay, lastDay);
})();
