document.addEventListener("DOMContentLoaded", function () {
    const ctx = document.getElementById("ordersReadBar");

    if (!ctx) {
        console.error("Canvas not found.");
        return;
    }

    fetch('/orders/bar-data')
        .then(response => response.json())
        .then(data => {
            const chart = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
                    datasets: [
                      {
                      label: "Last Year",
                      backgroundColor: "rgba(255, 165, 0, 0.8)",
                      data: data.lastYearData
                      },
                      {
                      label: "This Year",
                      backgroundColor: "rgba(0, 0, 255, 0.8)",
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
