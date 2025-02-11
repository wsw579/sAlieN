document.addEventListener("DOMContentLoaded", function () {
    const ctx = document.getElementById("accountReadChart");

    if (!ctx) {
        console.error("Canvas not found.");
        return;
    }

    fetch('/account/chart-data')
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
                               label: "Last Year",
                               lineTension: 0.3,
                               backgroundColor: "rgba(255, 165, 0,0.5)",
                               borderColor: "rgba(255, 165, 0,1)",
                               pointRadius: 5,
                               pointBackgroundColor: "rgba(255, 165, 0,1)",
                               data: lastYearData,
                           },
                           {
                               label: "This Year",
                               lineTension: 0.3,
                               backgroundColor: "rgba(0, 0, 255, 0.5)",
                               borderColor: "rgba(0, 0, 255,1)",
                               pointRadius: 5,
                               pointBackgroundColor: "rgba(0, 0, 255,1)",
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
