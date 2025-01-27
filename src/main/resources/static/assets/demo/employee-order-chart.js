(async function () {
    const loggedInUser = await fetchLoggedInUser();
    if (!loggedInUser) {
        alert('로그인된 사용자 정보를 가져올 수 없습니다.');
        return;
    }

    const { team, dept } = loggedInUser;

    const now = new Date();
    let selectedYear = now.getFullYear();
    let selectedMonth = now.getMonth();

    const getFirstAndLastDay = (year, month) => {
        const firstDay = new Date(year, month, 1).toISOString().split('T')[0];
        const lastDay = new Date(year, month + 1, 0).toISOString().split('T')[0];
        return { firstDay, lastDay };
    };

    const { firstDay, lastDay } = getFirstAndLastDay(selectedYear, selectedMonth);

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
        console.log("Generated API URL:", apiUrl);
        return apiUrl;
    };

    let chartInstance = null;

    const updateChart = async (startDate, endDate) => {
        const dynamicApiUrl = buildApiUrl(startDate, endDate);
        if (!dynamicApiUrl) {
            alert('API URL 생성 실패. 팀 또는 부서 ID 확인.');
            return;
        }

        try {
            console.log("Fetching data from:", dynamicApiUrl);
            const response = await fetch(dynamicApiUrl);
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
            }
            let data = await response.json();
            console.log("Fetched data:", data);

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
                            stack: 'Stack 0',
                        },
                        {
                            label: 'Remaining Orders',
                            data: totalOrders.map((total, index) => total - completedOrders[index]),
                            backgroundColor: 'rgba(54, 162, 235, 0.6)',
                            borderColor: 'rgba(54, 162, 235, 1)',
                            borderWidth: 1,
                            stack: 'Stack 0',
                        }
                    ]
                },
                options: {
                    plugins: {
                        tooltip: {
                            callbacks: {
                                label: function (context) {
                                    const label = context.dataset.label || '';
                                    const value = context.raw;
                                    return `${label}: ${value}`;
                                }
                            }
                        }
                    },
                    scales: {
                        x: {
                            stacked: true,
                        },
                        y: {
                            stacked: true,
                            beginAtZero: true,
                        }
                    }
                }
            });
        } catch (error) {
            console.error("Error fetching order data:", error);
        }
    };

    // 월 변경 버튼 추가
    const monthlySalesHeader = document.getElementById('monthlySalesHeader');
    if (!monthlySalesHeader) {
        console.error("monthlySalesHeader 요소를 찾을 수 없습니다.");
        return;
    }

    const monthControlDiv = document.createElement('div');
    monthControlDiv.innerHTML = `
        <button id="prevMonthBtn">&lt;</button>
        <span id="selectedMonth">${selectedMonth + 1}</span>월
        <button id="nextMonthBtn">&gt;</button>
    `;
    monthlySalesHeader.appendChild(monthControlDiv);

    const prevMonthBtn = document.getElementById('prevMonthBtn');
    const nextMonthBtn = document.getElementById('nextMonthBtn');
    const selectedMonthSpan = document.getElementById('selectedMonth');

    const changeMonth = (direction) => {
        selectedMonth += direction;
        if (selectedMonth < 0) {
            selectedMonth = 11;
            selectedYear -= 1;
        } else if (selectedMonth > 11) {
            selectedMonth = 0;
            selectedYear += 1;
        }
        selectedMonthSpan.textContent = selectedMonth + 1;

        console.log(`Changed to: ${selectedYear}-${selectedMonth + 1}`);
        const { firstDay: newFirstDay, lastDay: newLastDay } = getFirstAndLastDay(selectedYear, selectedMonth);
        updateChart(newFirstDay, newLastDay);
    };

    prevMonthBtn.addEventListener('click', () => changeMonth(-1));
    nextMonthBtn.addEventListener('click', () => changeMonth(1));

    updateChart(firstDay, lastDay);
})();
