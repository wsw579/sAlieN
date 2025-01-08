
    document.addEventListener("DOMContentLoaded", function() {
        // Set new default font family and font color to mimic Bootstrap's default styling
        Chart.defaults.global.defaultFontFamily = '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
        Chart.defaults.global.defaultFontColor = '#292b2c';

        // 현재 연도와 작년 연도 설정
        var currentYear = new Date().getFullYear();
        var lastYear = currentYear - 1;

        // 테이블 데이터를 추출하여 차트 데이터로 변환
        var table = document.getElementById("datatablesSimple");
        if (!table) {
            console.error("Table with ID 'datatablesSimple' not found.");
            return;
        }

        var rows = table.getElementsByTagName("tbody")[0].getElementsByTagName("tr");
        var lastYearData = {};
        var currentYearData = {};
        for (var i = 0; i < rows.length; i++) {
            var createdDate = new Date(rows[i].getElementsByTagName("td")[2].innerText);
            var month = createdDate.getMonth(); // 월은 0부터 시작합니다.
            var year = createdDate.getFullYear();
            var status = rows[i].getElementsByTagName("td")[4].innerText;

            if (year == lastYear && status === "completed") {
                if (lastYearData[month]) {
                    lastYearData[month]++;
                } else {
                    lastYearData[month] = 1;
                }
            }

            if (year == currentYear && status === "completed") {
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
        var ctx = document.getElementById("ordersReadChart");
        if (!ctx) {
            console.error("Canvas with ID 'ordersReadChart' not found.");
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
