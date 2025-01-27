document.addEventListener("DOMContentLoaded", function () {
    const ctx = document.getElementById("leadsReadBar");

    if (!ctx) {
        console.error("Canvas not found.");
        return;
    }

    fetch('/leads/bar-data')
        .then(response => response.json())
        .then(data => {
            const chart = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
                    datasets: [
                        {
                            label: "Last Year",
                            backgroundColor: "rgba(2,117,216,1)",
                            data: data.lastYearData
                        },
                        {
                            label: "This Year",
                            backgroundColor: "rgba(255,0,0,1)",
                            data: data.currentYearData
                        }
                    ]
                },
                options: {
                    responsive: true,
                    animation: { duration: 500 },
                    plugins: { legend: { display: true } },
                    scales: {
                        x: { grid: { display: false } },
                        y: { beginAtZero: true }
                    }
                }
            });
        })
        .catch(error => console.error("Error loading chart data:", error));
});
