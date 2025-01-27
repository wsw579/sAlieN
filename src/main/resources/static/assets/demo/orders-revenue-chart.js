document.addEventListener("DOMContentLoaded", function () {
    const ctx = document.getElementById("ordersRevenueChart");

    if (!ctx) {
        console.error("Canvas not found.");
        return;
    }

    fetch('/orders/revenue-chart-data')
        .then(response => response.json())
        .then(data => {
            const labels = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
            const lastYearData = data.lastYearData.map(value => value / 1000); // 값을 10000으로 나눔
            const currentYearData = data.currentYearData.map(value => value / 1000); // 값을 10000으로 나눔

            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [
                        {
                            label: "Last Year",
                            lineTension: 0.3,
                            backgroundColor: "rgba(2,117,216,0.2)",
                            borderColor: "rgba(2,117,216,1)",
                            pointRadius: 5,
                            pointBackgroundColor: "rgba(2,117,216,1)",
                            data: lastYearData,
                        },
                        {
                            label: "This Year",
                            lineTension: 0.3,
                            backgroundColor: "rgba(255,0,0,0.2)",
                            borderColor: "rgba(255,0,0,1)",
                            pointRadius: 5,
                            pointBackgroundColor: "rgba(255,0,0,1)",
                            data: currentYearData,
                        }
                    ]
                },
                options: {
                    scales: {
                        xAxes: [{ // Chart.js 2.x에서는 x축 설정에 xAxes 사용
                            gridLines: { display: false }
                        }],
                        yAxes: [{ // Chart.js 2.x에서는 y축 설정에 yAxes 사용
                            ticks: {
                                beginAtZero: true,
                                callback: function (value, index, values) {
                                    return value + " k"; // 눈금 값 뒤에 "k" 추가
                                }
                            }
                        }]
                    },
                    legend: {
                        display: true
                    },
                    tooltips: { // 툴팁 설정 추가 가능
                        callbacks: {
                            label: function (tooltipItem, data) {
                                const dataset = data.datasets[tooltipItem.datasetIndex];
                                const value = dataset.data[tooltipItem.index];
                                return `${dataset.label}: ${value} k`; // 툴팁에 단위 추가
                            }
                        }
                    }
                }
            });
        })
        .catch(error => console.error("Error fetching chart data:", error));
});
