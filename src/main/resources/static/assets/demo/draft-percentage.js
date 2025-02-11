document.addEventListener("DOMContentLoaded", function () {
    let selectedYear = new Date().getFullYear();
    let selectedMonth = new Date().getMonth() + 1;
    let maxYear = selectedYear;
    let maxMonth = selectedMonth; // 현재 월을 최대 월로 설정
    let draftChartInstance = null;

    const draftChartHeader = document.getElementById("draftChartHeader");
    if (!draftChartHeader) {
        console.error("⚠️ 'draftChartHeader' 요소를 찾을 수 없습니다.");
        return;
    }

    // 📌 연도 선택 & 월 변경 버튼 추가
    const monthControlDiv = document.createElement("div");
    monthControlDiv.classList.add("d-flex", "align-items-center", "mx-auto");
    monthControlDiv.innerHTML = `
        <button id="prevMonthBtn" class="btn btn-outline-secondary btn-sm mx-1">&lt;</button>
        <span id="selectedMonthYear" class="mx-2">${selectedYear}년 ${selectedMonth}월</span>
        <button id="nextMonthBtn" class="btn btn-outline-secondary btn-sm mx-1">&gt;</button>
    `;

    draftChartHeader.classList.add("d-flex", "align-items-center", "position-relative");
    draftChartHeader.appendChild(monthControlDiv);
    monthControlDiv.style.position = "absolute";
    monthControlDiv.style.left = "50%";

    const prevMonthBtn = document.getElementById("prevMonthBtn");
    const nextMonthBtn = document.getElementById("nextMonthBtn");
    const selectedMonthYear = document.getElementById("selectedMonthYear");

    function updateNavigation() {
        selectedMonthYear.textContent = `${selectedYear}년 ${selectedMonth}월`;

        prevMonthBtn.disabled = selectedYear === 2020 && selectedMonth === 1;
        nextMonthBtn.disabled = selectedYear === maxYear && selectedMonth >= maxMonth; // ✅ maxMonth 초과 방지
    }

    async function fetchAndRenderDraftChart(year, month) {
        console.log("📢 API 호출:", `/api/draft-percentage?year=${year}&month=${month}`);

        try {
            const response = await fetch(`/api/draft-percentage?year=${year}&month=${month}`, {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' }
            });

            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

            const data = await response.json();
            console.log("📢 API 응답 데이터:", data);

            let draftPercentage = (data && data.draftPercentage !== undefined) ? data.draftPercentage : 0;
            console.warn(`⚠️ ${year}년 ${month}월 데이터 없음 → 기본값 0% 적용`);

            const ctx = document.getElementById('draftChart').getContext('2d');

            if (draftChartInstance) {
                draftChartInstance.destroy();
                draftChartInstance = null;
            }

            const centerTextPlugin = {
                id: 'centerText',
                beforeDraw(chart) {
                    const { width, height, ctx } = chart;
                    ctx.restore();
                    const percentText = `${draftPercentage.toFixed(0)}%`;
                    ctx.font = 'bold 24px Arial';
                    ctx.textBaseline = 'middle';
                    ctx.fillStyle = '#000';
                    const textX = Math.round((width - ctx.measureText(percentText).width) / 2);
                    const textY = height / 2 + 10;
                    ctx.fillText(percentText, textX, textY);
                    const labelText = 'Close 비율';
                    ctx.font = '14px Arial';
                    ctx.fillStyle = '#666';
                    const labelX = Math.round((width - ctx.measureText(labelText).width) / 2);
                    const labelY = height / 2 + 40;
                    ctx.fillText(labelText, labelX, labelY);
                    ctx.save();
                }
            };

            draftChartInstance = new Chart(ctx, {
                type: 'doughnut',
                data: {
                    labels: ['Completed', 'Draft'],
                    datasets: [{
                        data: [draftPercentage, 100 - draftPercentage],
                        backgroundColor: ['#17a2b8', '#d4f1f9'],
                        borderWidth: 0,
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    cutoutPercentage: 60,
                    legend: {
                        display: true,
                        labels: {
                            boxWidth: 15,
                            padding: 20,
                            fontSize: 14
                        }
                    },
                    tooltips: {
                        enabled: true,
                        callbacks: {
                            label: function (tooltipItem, data) {
                                const dataset = data.datasets[tooltipItem.datasetIndex];
                                const value = dataset.data[tooltipItem.index];
                                const label = data.labels[tooltipItem.index];
                                return `${label}: ${value.toFixed(0)}%`;
                            }
                        }
                    }
                },
                plugins: [centerTextPlugin]
            });

        } catch (error) {
            console.error("⚠️ 도넛 차트 데이터 가져오기 실패:", error);
            console.warn(`⚠️ ${year}년 ${month}월 데이터 없음 → 기본값 0% 적용`);
            renderDefaultDraftChart();
        }
    }

    function renderDefaultDraftChart() {
        const ctx = document.getElementById('draftChart').getContext('2d');

        if (draftChartInstance) {
            draftChartInstance.destroy();
            draftChartInstance = null;
        }

        draftChartInstance = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: ['Completed', 'Draft'],
                datasets: [{
                    data: [0, 100],
                    backgroundColor: ['#17a2b8', '#d4f1f9'],
                    borderWidth: 0,
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                cutoutPercentage: 60,
                legend: {
                    display: true,
                    labels: {
                        boxWidth: 15,
                        padding: 20,
                        fontSize: 14
                    }
                },
                tooltips: {
                    enabled: true,
                    callbacks: {
                        label: function (tooltipItem, data) {
                            const dataset = data.datasets[tooltipItem.datasetIndex];
                            const value = dataset.data[tooltipItem.index];
                            const label = data.labels[tooltipItem.index];
                            return `${label}: ${value.toFixed(0)}%`;
                        }
                    }
                }
            },
            plugins: [{
                id: 'centerText',
                beforeDraw(chart) {
                    const { width, height, ctx } = chart;
                    ctx.restore();
                    const percentText = `0%`;
                    ctx.font = 'bold 24px Arial';
                    ctx.textBaseline = 'middle';
                    ctx.fillStyle = '#000';
                    const textX = Math.round((width - ctx.measureText(percentText).width) / 2);
                    const textY = height / 2 + 10;
                    ctx.fillText(percentText, textX, textY);
                    const labelText = 'Close 비율';
                    ctx.font = '14px Arial';
                    ctx.fillStyle = '#666';
                    const labelX = Math.round((width - ctx.measureText(labelText).width) / 2);
                    const labelY = height / 2 + 40;
                    ctx.fillText(labelText, labelX, labelY);
                    ctx.save();
                }
            }]
        });
    }

    function changeMonth(delta) {
        let newMonth = selectedMonth + delta;
        let newYear = selectedYear;

        if (newMonth < 1) {
            newYear--;
            newMonth = 12;
        } else if (newMonth > 12) {
            if (selectedYear === maxYear && newMonth > maxMonth) {
                return; // ✅ maxMonth 초과 방지
            }
            newYear++;
            newMonth = 1;
        }

        selectedYear = newYear;
        selectedMonth = newMonth;

        updateNavigation();
        fetchAndRenderDraftChart(selectedYear, selectedMonth);
    }

    prevMonthBtn.addEventListener("click", () => changeMonth(-1));
    nextMonthBtn.addEventListener("click", () => changeMonth(1));

    async function initialize() {
        updateNavigation();
        await fetchAndRenderDraftChart(selectedYear, selectedMonth);
    }

    initialize();
});
