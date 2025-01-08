
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
        var lastYearData = Array(12).fill(0);
        var currentYearData = Array(12).fill(0);

        for (var i = 0; i < rows.length; i++) {
            var createdDate = new Date(rows[i].getElementsByTagName("td")[2].innerText);
            var month = createdDate.getMonth(); // 월은 0부터 시작합니다.
            var year = createdDate.getFullYear();
            var status = rows[i].getElementsByTagName("td")[4].innerText;

            if (year == lastYear && status === "completed") {
                lastYearData[month]++;
            }

            if (year == currentYear && status === "completed") {
                currentYearData[month]++;
            }
        }

        // 누적 값을 계산
        for (var month = 1; month < 12; month++) {
            lastYearData[month] += lastYearData[month - 1];
            currentYearData[month] += currentYearData[month - 1];
        }

        var labels = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

        // Bar Chart Example
        var ctx = document.getElementById("ordersReadBar");
        if (!ctx) {
            console.error("Canvas with ID 'ordersReadBar' not found.");
            return;
        }

        var myBarChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [
                    {
                        label: "Last Year (Blue)",
                        backgroundColor: "rgba(2,117,216,1)",
                        borderColor: "rgba(2,117,216,1)",
                        data: lastYearData,
                    },
                    {
                        label: "This Year (Red)",
                        backgroundColor: "rgba(255,0,0,1)",
                        borderColor: "rgba(255,0,0,1)",
                        data: currentYearData,
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
                            display: true
                        }
                    }],
                },
                legend: {
                    display: true // 범례를 표시합니다.
                }
            }
        });
    });

