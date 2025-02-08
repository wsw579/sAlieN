document.addEventListener("DOMContentLoaded", function () {
    const ctx = document.getElementById("contractsReadChart");

    if (!ctx) {
        console.error("Canvas not found.");
        return;
    }

    fetch('/contracts/chart-data')
        .then(response => response.json())
        .then(data => {
            const labels = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
            const lastYearData = data.lastYearData;
            const currentYearData = data.currentYearData;

            new Chart(ctx, {
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
                            data: lastYearData,
                        },
                        {
                            label: "This Year (Red)",
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
                        x: { grid: { display: false } },
                        y: { beginAtZero: true },
                    },
                    legend: { display: true }
                }
            });
        })
        .catch(error => console.error("Error fetching chart data:", error));
});
