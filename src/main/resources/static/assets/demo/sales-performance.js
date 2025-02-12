document.addEventListener("DOMContentLoaded", function () {
    let selectedYear = new Date().getFullYear();
    let selectedMonth = new Date().getMonth() + 1;
    let maxYear = selectedYear;
    let maxMonth = selectedMonth;

    const salesPerformanceHeader = document.getElementById("salesPerformanceHeader");
    if (!salesPerformanceHeader) {
        console.error("⚠️ 'salesPerformanceHeader' 요소를 찾을 수 없습니다.");
        return;
    }

    // 📌 연도 선택 & 월 변경 버튼 추가
    const monthControlDiv = document.createElement("div");
    monthControlDiv.classList.add("d-flex", "align-items-center", "mx-auto");
    monthControlDiv.innerHTML = `
        <button id="prevMonthBtn" class="btn btn-outline-secondary btn-sm">&lt;</button>
        <span id="selectedMonthYear" class="mx-2">${selectedYear}년 ${selectedMonth}월</span>
        <button id="nextMonthBtn" class="btn btn-outline-secondary btn-sm">&gt;</button>
    `;

    salesPerformanceHeader.classList.add("d-flex", "align-items-center", "position-relative");
    salesPerformanceHeader.appendChild(monthControlDiv);
    monthControlDiv.style.position = "absolute";
    monthControlDiv.style.left = "50%";
    monthControlDiv.style.transform = "translateX(-50%)";

    // 📌 버튼 및 요소 가져오기
    const prevMonthBtn = document.getElementById("prevMonthBtn");
    const nextMonthBtn = document.getElementById("nextMonthBtn");
    const selectedMonthYear = document.getElementById("selectedMonthYear");

    let topSalesChart = null;
    let bottomSalesChart = null;

    // 📌 최소 연도 설정
    const minYear = 2020;

    function updateNavigation() {
        selectedMonthYear.textContent = `${selectedYear}년 ${selectedMonth}월`;
        prevMonthBtn.disabled = selectedYear === minYear && selectedMonth === 1;
        nextMonthBtn.disabled = selectedYear === maxYear && selectedMonth >= maxMonth;
    }

    async function fetchAndRenderData(year, month) {
        console.log("📢 API 호출:", `/api/sales-performance?year=${year}&month=${month}`);

        try {
            const response = await fetch(`/api/sales-performance?year=${year}&month=${month}`, {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' }
            });

            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

            const data = await response.json();
            console.log("📢 API 응답 데이터:", data);

            // 📌 데이터가 없는 경우 그래프만 빈 데이터로 유지
            if (!data || data.length === 0) {
                console.warn(`⚠️ ${year}년 ${month}월 데이터 없음`);

                updateChart("topSalesChart", "상위 5명 영업 실적", [], [], "rgba(54, 162, 235, 0.2)", "rgba(54, 162, 235, 1)");
                updateChart("bottomSalesChart", "하위 5명 영업 실적", [], [], "rgba(255, 99, 132, 0.2)", "rgba(255, 99, 132, 1)");

                return;
            }

            // 데이터 키 확인 (부서/팀/직원 구분)
            const keyName = data[0].departmentName ? "departmentName"
                : data[0].teamName ? "teamName"
                : "employeeName"; // 기본값은 직원

            const sortedData = data.sort((a, b) => b.totalSales - a.totalSales);
            const top5Data = sortedData.slice(0, 5);
            const bottom5Data = sortedData.slice(-5);

            const top5Labels = top5Data.map(item => item[keyName]);
            const top5Sales = top5Data.map(item => item.totalSales / 1000);

            const bottom5Labels = bottom5Data.map(item => item[keyName]);
            const bottom5Sales = bottom5Data.map(item => item.totalSales / 1000);

            updateChart("topSalesChart", "상위 5명 영업 실적", top5Labels, top5Sales, "rgba(54, 162, 235, 0.2)", "rgba(54, 162, 235, 1)");
            updateChart("bottomSalesChart", "하위 5명 영업 실적", bottom5Labels, bottom5Sales, "rgba(255, 99, 132, 0.2)", "rgba(255, 99, 132, 1)");
        } catch (error) {
            console.error("⚠️ 영업 실적 데이터 가져오기 실패:", error);
        }
    }

    function updateChart(canvasId, label, labels, sales, bgColor, borderColor) {
        const canvas = document.getElementById(canvasId);
        if (!canvas) {
            console.error(`⚠️ '${canvasId}' 요소를 찾을 수 없습니다.`);
            return;
        }

        const ctx = canvas.getContext('2d');

        if (canvasId === "topSalesChart" && topSalesChart) {
            topSalesChart.destroy();
        } else if (canvasId === "bottomSalesChart" && bottomSalesChart) {
            bottomSalesChart.destroy();
        }

        // 📌 빈 데이터일 경우 기본값 설정
        const hasData = sales.length > 0;
        const adjustedLabels = hasData ? labels : ["실적이 존재하지 않습니다."];
        const adjustedSales = hasData ? sales : [0];

        const newChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: adjustedLabels,
                datasets: [{
                    label: label,
                    data: adjustedSales,
                    backgroundColor: bgColor,
                    borderColor: borderColor,
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                scales: {
                    xAxes: [{
                        ticks: { autoSkip: false }
                    }],
                    yAxes: [{
                        ticks: {
                            beginAtZero: true,
                            min: 0, // **Y축 최소값을 0으로 고정**
                            suggestedMax: hasData ? Math.max(...sales) * 1.1 : 1, // **최대값 자동 조정 및 10% 여유 추가**
                            callback: function (value) {
                                return value.toLocaleString() + " K"; // **Y축 값에 , 및 K 단위 추가**
                            }
                        }
                    }]
                },
                tooltips: {
                    callbacks: {
                        label: function (tooltipItem, data) {
                            const dataset = data.datasets[tooltipItem.datasetIndex];
                            const value = dataset.data[tooltipItem.index];
                            return `${dataset.label}: ${value.toLocaleString()} K`; // **툴팁 값에도 , 및 K 단위 추가**
                        }
                    }
                }
            }
        });

        if (canvasId === "topSalesChart") {
            topSalesChart = newChart;
        } else if (canvasId === "bottomSalesChart") {
            bottomSalesChart = newChart;
        }
    }

    function changeMonth(delta) {
        selectedMonth += delta;
        if (selectedMonth < 1) {
            if (selectedYear > minYear) {
                selectedYear--;
                selectedMonth = 12;
            } else return;
        } else if (selectedMonth > 12) {
            if (selectedYear < maxYear) {
                selectedYear++;
                selectedMonth = 1;
            } else return;
        }

        updateNavigation();
        fetchAndRenderData(selectedYear, selectedMonth);
    }

    prevMonthBtn.addEventListener("click", () => changeMonth(-1));
    nextMonthBtn.addEventListener("click", () => changeMonth(1));

    async function initialize() {
        updateNavigation();
        updateChart("topSalesChart", "상위 5명 영업 실적", [], [], "rgba(54, 162, 235, 0.2)", "rgba(54, 162, 235, 1)");
        updateChart("bottomSalesChart", "하위 5명 영업 실적", [], [], "rgba(255, 99, 132, 0.2)", "rgba(255, 99, 132, 1)");
        await fetchAndRenderData(selectedYear, selectedMonth);
    }

    initialize();
});
