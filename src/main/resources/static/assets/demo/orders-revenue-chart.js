document.addEventListener("DOMContentLoaded", function () {
    let selectedYear = new Date().getFullYear();
    let minYear = selectedYear;
    let maxYear = selectedYear;

    const monthlySalesHeader = document.getElementById("monthlySalesChartUserHeader");
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
        selectedYearSpan.textContent = selectedYear;
        prevYearBtn.disabled = selectedYear <= minYear;
        nextYearBtn.disabled = selectedYear >= maxYear;
    }

    let chartInstance = null;

    async function fetchAndRenderData(year) {
        const loggedInUser = await fetchLoggedInUser();
        if (!loggedInUser) {
            alert("로그인된 사용자 정보를 가져올 수 없습니다.");
            return;
        }

        const { team, dept } = loggedInUser;
        let apiUrl = `/api/monthly-revenue-purchase?year=${year}`;

        if (team) apiUrl += `&team=${encodeURIComponent(team)}`;
        if (dept) apiUrl += `&department=${encodeURIComponent(dept)}`;

        try {
            const response = await fetch(apiUrl);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            console.log(`📢 ${year}년 데이터:`, data);

            // ✅ 데이터가 없을 경우 기본값(0)으로 설정
            const labels = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
            const revenues = new Array(12).fill(0);
            const purchases = new Array(12).fill(0);
            const profits = new Array(12).fill(0);

            if (data.labels && data.revenues) {
                data.labels.forEach((month, index) => {
                    const monthIndex = parseInt(month.split("-")[1], 10) - 1;
                    revenues[monthIndex] = (data.revenues[index] || 0) / 1000;
                    purchases[monthIndex] = (data.purchases[index] || 0) / 1000;
                    profits[monthIndex] = revenues[monthIndex] - purchases[monthIndex];
                });
            } else {
                console.warn(`⚠️ ${year}년 데이터가 없음 → 기본값 0으로 설정`);
            }

            // ✅ 기존 차트가 있으면 업데이트, 없으면 새로 생성
            if (chartInstance) {
                chartInstance.data.labels = labels;
                chartInstance.data.datasets[0].data = revenues;
                chartInstance.data.datasets[1].data = purchases;
                chartInstance.data.datasets[2].data = profits;
                chartInstance.update();
            } else {
                const ctx = document.getElementById("monthlySalesChartUser").getContext("2d");
                chartInstance = new Chart(ctx, {
                    type: "bar",
                    data: {
                        labels: labels,
                        datasets: [
                            {
                                label: "매출",
                                data: revenues,
                                backgroundColor: "rgba(2,117,216,0.2)",
                                borderColor: "rgba(2,117,216,1)",
                                borderWidth: 1,
                            },
                            {
                                label: "매입",
                                data: purchases,
                                backgroundColor: "rgba(255,0,0,0.2)",
                                borderColor: "rgba(255,0,0,1)",
                                borderWidth: 1,
                            },
                            {
                                label: "영업이익",
                                data: profits,
                                type: "line",
                                backgroundColor: "rgba(2,117,216,0.2)",
                                borderColor: "rgba(2,117,216,1)",
                                tension: 0.3,
                                pointRadius: 5,
                                pointBackgroundColor: "rgba(2,117,216,1)",
                                borderWidth: 2,
                            },
                        ],
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        scales: {
                            xAxes: [
                                {
                                    gridLines: {
                                        display: false,
                                    },
                                },
                            ],
                            yAxes: [
                                {
                                    ticks: {
                                        beginAtZero: true,
                                        callback: function (value) {
                                            return `${value.toLocaleString()} K`;
                                        },
                                    },
                                },
                            ],
                        },
                        legend: {
                            display: true,
                        },
                        tooltips: {
                            callbacks: {
                                label: function (tooltipItem, data) {
                                    const dataset = data.datasets[tooltipItem.datasetIndex];
                                    const value = dataset.data[tooltipItem.index];
                                    return `${dataset.label}: ${value.toLocaleString()} K`;
                                },
                            },
                        },
                        events: ["click"],
                    },
                });
            }
        } catch (error) {
            console.error("⚠️ 매출/매입 데이터 가져오기 실패:", error.message || error);
            alert("데이터를 가져오는 데 실패했습니다.");
        }
    }

    window.changeYear = async function (delta) {
        console.log("changeYear called with delta:", delta);
        const newYear = selectedYear + delta;
        if (newYear < minYear || newYear > maxYear) {
            return;
        }

        selectedYear = newYear;
        updateYearNavigation();
        await fetchAndRenderData(selectedYear);
    };

    async function initialize() {
        await fetchAvailableYears();
        updateYearNavigation();
        await fetchAndRenderData(selectedYear);
    }

    initialize();
});

// ✅ 로그인 사용자 정보 가져오기
async function fetchLoggedInUser() {
    try {
        const response = await fetch("/api/getLoggedInUser");
        if (!response.ok) {
            throw new Error("Failed to fetch logged-in user");
        }
        return await response.json();
    } catch (error) {
        console.error("⚠️ 로그인된 사용자 정보 가져오기 실패:", error);
        return null;
    }
}
