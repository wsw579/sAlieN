document.addEventListener("DOMContentLoaded", function () {
    const ctx = document.getElementById("opportunitiesReadBar");

        var labels = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

        // Bar Chart Example
        var ctx = document.getElementById("opportunitiesReadBar");
        if (!ctx) {
            console.error("Canvas with ID 'opportunitiesReadBar' not found.");
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

