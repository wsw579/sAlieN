document.addEventListener("DOMContentLoaded", function () {
    fetch('/api/sales-performance')
        .then(response => response.json())
        .then(data => {
            // totalSales 기준으로 내림차순 정렬
            const sortedData = data.sort((a, b) => b.totalSales - a.totalSales);

            // 상위 5명과 하위 5명 추출
            const top5Data = sortedData.slice(0, 5);
            const bottom5Data = sortedData.slice(-5);

            // 상위 5명 데이터
            const top5Labels = top5Data.map(item => item.employeeName);
            const top5Sales = top5Data.map(item => item.totalSales);

            // 하위 5명 데이터
            const bottom5Labels = bottom5Data.map(item => item.employeeName);
            const bottom5Sales = bottom5Data.map(item => item.totalSales);

            // 상위 5명 그래프
            const top5Ctx = document.getElementById('topSalesChart').getContext('2d');
            new Chart(top5Ctx, {
                type: 'bar',
                data: {
                    labels: top5Labels,
                    datasets: [{
                        label: '상위 5명 영업 실적',
                        data: top5Sales,
                        backgroundColor: 'rgba(54, 162, 235, 0.2)',
                        borderColor: 'rgba(54, 162, 235, 1)',
                        borderWidth: 1
                    }]
                },
                options: {
                    scales: {
                        y: {
                            beginAtZero: true
                        }
                    }
                }
            });

            // 하위 5명 그래프
            const bottom5Ctx = document.getElementById('bottomSalesChart').getContext('2d');
            new Chart(bottom5Ctx, {
                type: 'bar',
                data: {
                    labels: bottom5Labels,
                    datasets: [{
                        label: '하위 5명 영업 실적',
                        data: bottom5Sales,
                        backgroundColor: 'rgba(255, 99, 132, 0.2)',
                        borderColor: 'rgba(255, 99, 132, 1)',
                        borderWidth: 1
                    }]
                },
                options: {
                    scales: {
                        y: {
                            beginAtZero: true
                        }
                    }
                }
            });
        })
        .catch(error => console.error('Error:', error));
});
