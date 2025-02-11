document.addEventListener("DOMContentLoaded", function () {
    let selectedYear = new Date().getFullYear();
    let minYear = selectedYear;
    let maxYear = selectedYear;

    const monthlySalesHeader = document.getElementById('monthlySalesChartHeader');
    if (!monthlySalesHeader) {
        console.error("⚠️ 'monthlySalesChartUserHeader' 요소를 찾을 수 없습니다.");
        return;
    }

    const yearControlDiv = document.createElement("div");
    yearControlDiv.classList.add("d-flex", "align-items-center", "mx-auto");
    yearControlDiv.innerHTML = `
        <button id="prevYearBtn" class="btn btn-outline-secondary btn-sm mx-1">&lt;</button>
        <span id="selectedYear" class="mx-2">${selectedYear}</span>
        <button id="nextYearBtn" class="btn btn-outline-secondary btn-sm mx-1">&gt;</button>
    `;

    monthlySalesHeader.classList.add("d-flex", "align-items-center", "position-relative");
    monthlySalesHeader.appendChild(yearControlDiv);
    yearControlDiv.style.position = "absolute";
    yearControlDiv.style.left = "50%";
    yearControlDiv.style.transform = "translateX(-50%)";

    const prevYearBtn = document.getElementById("prevYearBtn");
    const nextYearBtn = document.getElementById("nextYearBtn");
    const selectedYearSpan = document.getElementById("selectedYear");

    prevYearBtn.addEventListener("click", function () {
        changeYear(-1);
    });

    nextYearBtn.addEventListener("click", function () {
        changeYear(1);
    });

    async function fetchAvailableYears() {
        try {
            const response = await fetch("/api/available-years");
            if (!response.ok) {
                throw new Error("Failed to fetch available years");
            }
            const data = await response.json();
            minYear = data.minYear;
            maxYear = data.maxYear;
        } catch (error) {
            console.error("Error fetching available years:", error.message || error);
        }
    }

    function updateYearNavigation() {
        // 연도 업데이트
        selectedYearSpan.textContent = selectedYear;

        // 이전 버튼 활성화/비활성화
        prevYearBtn.disabled = selectedYear <= minYear;

        // 다음 버튼 활성화/비활성화
        nextYearBtn.disabled = selectedYear >= maxYear;
    }

let chartInstance = null;

async function fetchAndRenderData(year) {
    const loggedInUser = await fetchLoggedInUser();
    if (!loggedInUser) {
        alert('로그인된 사용자 정보를 가져올 수 없습니다.');
        return;
    }

    const { team, dept } = loggedInUser;
    let apiUrl = `/api/monthly-revenue-purchase?year=${year}`;

    if (team) {
        apiUrl += `&team=${encodeURIComponent(team)}`;
    }
    if (dept) {
        apiUrl += `&department=${encodeURIComponent(dept)}`;
    }


    try {
        const response = await fetch(apiUrl);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

       const data = await response.json();
       console.log(`Fetched data for ${year}:`, data);
        // 레이블을 고정된 월 이름으로 설정
        const labels = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
        const revenues = new Array(12).fill(0); // 매출 데이터를 12개월로 초기화
        const purchases = new Array(12).fill(0); // 매입 데이터를 12개월로 초기화
        const profits = new Array(12).fill(0); // 순이익 데이터를 12개월로 초기화

        // API에서 받은 데이터를 월별로 매핑
        data.labels.forEach((month, index) => {
            const monthIndex = parseInt(month.split("-")[1], 10) - 1; // 월 부분 추출 (0부터 시작)
            revenues[monthIndex] = data.revenues[index] / 1000; // "천원" 단위로 변환
            purchases[monthIndex] = data.purchases[index] / 1000; // "천원" 단위로 변환
            profits[monthIndex] = revenues[monthIndex] - purchases[monthIndex]; // 순이익 계산
        });

        // 기존 차트가 존재하면 업데이트
        if (chartInstance) {
            chartInstance.data.labels = labels; // 고정된 레이블 사용
            chartInstance.data.datasets[0].data = revenues;
            chartInstance.data.datasets[1].data = purchases;
            chartInstance.data.datasets[2].data = profits; // 순이익 데이터 업데이트
            chartInstance.update(); // 차트 업데이트
        } else {
            const ctx = document.getElementById('monthlySalesChart').getContext('2d');
            chartInstance = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: labels, // 고정된 레이블 사용
                    datasets: [
                        {
                            label: '매출',
                            data: revenues,
                            backgroundColor: 'rgba(2,117,216,0.2)',
                            borderColor: 'rgba(2,117,216,1)',
                            borderWidth: 1
                        },
                        {
                            label: '매입',
                            data: purchases,
                            backgroundColor: 'rgba(255,0,0,0.2)',
                            borderColor: 'rgba(255,0,0,1)',
                            borderWidth: 1
                        },
                        {
                            label: '영업이익',
                            data: profits,
                            type: 'line', // 라인 차트로 추가
                            backgroundColor: 'rgba(2,117,216,0.2)',
                            borderColor: 'rgba(2,117,216,1)',
                            tension: 0.3, // 곡선 정도
                            pointRadius: 5,
                            pointBackgroundColor: 'rgba(2,117,216,1)',
                            borderWidth: 2
                        }
                    ]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: {
                        xAxes: [{
                            gridLines: {
                                display: false // X축 그리드 숨김
                            }
                        }],
                        yAxes: [{
                            ticks: {
                                beginAtZero: true,
                                callback: function (value) {
                                    return `${value.toLocaleString()} K`; // Y축 눈금 값 포맷
                                }
                            }
                        }]
                    },
                    legend: {
                        display: true // 범례 표시
                    },
                    tooltips: {
                        callbacks: {
                            label: function (tooltipItem, data) {
                                const dataset = data.datasets[tooltipItem.datasetIndex];
                                const value = dataset.data[tooltipItem.index];
                                return `${dataset.label}: ${value.toLocaleString()} K`; // 툴팁 값 포맷
                            }
                        }
                    },
                    events: ['click'] // 'mousemove'를 제외하여 마우스 움직임에 반응하지 않음
                }
            });

        }
    } catch (error) {
        console.error('Error fetching sales data:', error.message || error);
        alert('매출/매입 데이터를 가져오는 데 실패했습니다.');
    }
}

    window.changeYear = async function (delta) {
        console.log('changeYear called with delta:', delta);
        const newYear = selectedYear + delta;
        if (newYear < minYear || newYear > maxYear) {
            return; // 연도 범위를 초과하면 아무 작업도 수행하지 않음
        }

        selectedYear = newYear;
        updateYearNavigation(); // 버튼 상태와 연도 업데이트
        await fetchAndRenderData(selectedYear);
    };

    async function initialize() {
        await fetchAvailableYears();
        updateYearNavigation();
        await fetchAndRenderData(selectedYear);
    }

    initialize();
});

// Helper function to fetch logged-in user details
async function fetchLoggedInUser() {
    try {
        const response = await fetch('/api/getLoggedInUser');
        if (!response.ok) {
            throw new Error('Failed to fetch logged-in user');
        }
        return await response.json();
    } catch (error) {
        console.error('Error fetching logged-in user:', error);
        return null;
    }
}
