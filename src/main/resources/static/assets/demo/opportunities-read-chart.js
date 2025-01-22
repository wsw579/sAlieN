document.addEventListener("DOMContentLoaded", function () {
    const ctx = document.getElementById("opportunitiesReadChart");

            if (year == currentYear && status === "Closed(won)") {
                if (currentYearData[month]) {
                    currentYearData[month]++;
                } else {
                    currentYearData[month] = 1;
                }
            }
        }

        var labels = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
        var lastYearChartData = [];
        var currentYearChartData = [];
        for (var month = 0; month < 12; month++) {
            lastYearChartData.push(lastYearData[month] || 0);
            currentYearChartData.push(currentYearData[month] || 0);
        }

        // Area Chart Example
        var ctx = document.getElementById("opportunitiesReadChart");
        if (!ctx) {
            console.error("Canvas with ID 'opportunitiesReadChart' not found.");
            return;
        }

        var myLineChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [
                    {
                        label: "Last Year (Blue)",
                        lineTension: 0.3,
                        backgroundColor: "rgba(2,117,216,0.2)",
                        borderColor: "rgba(2,117,216,1)",
                        pointRadius: 5,
                        pointBackgroundColor: "rgba(2,117,216,1)",
                        pointBorderColor: "rgba(255,255,255,0.8)",
                        pointHoverRadius: 5,
                        pointHoverBackgroundColor: "rgba(2,117,216,1)",
                        pointHitRadius: 50,
                        pointBorderWidth: 2,
                        data: lastYearChartData,
                    },
                    {
                        label: "This Year (Red)",
                        lineTension: 0.3,
                        backgroundColor: "rgba(255,0,0,0.2)",
                        borderColor: "rgba(255,0,0,1)",
                        pointRadius: 5,
                        pointBackgroundColor: "rgba(255,0,0,1)",
                        pointBorderColor: "rgba(255,255,255,0.8)",
                        pointHoverRadius: 5,
                        pointHoverBackgroundColor: "rgba(255,0,0,1)",
                        pointHitRadius: 50,
                        pointBorderWidth: 2,
                        data: currentYearChartData,
                    }
                ],
            },
            options: {
                scales: {
                    xAxes: [{
                        time: {
                            unit: 'month'
                        },
                        gridLines: {
                            display: false
                        },
                        ticks: {
                            maxTicksLimit: 12
                        }
                    }],
                    yAxes: [{
                        ticks: {
                            min: 0,
                            maxTicksLimit: 5
                        },
                        gridLines: {
                            color: "rgba(0, 0, 0, .125)",
                        }
                    }],
                },
                legend: {
                    display: true // 범례를 표시합니다.
                }
            }
        });
    });
