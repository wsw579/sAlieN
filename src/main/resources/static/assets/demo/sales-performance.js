document.addEventListener("DOMContentLoaded", function () {
    fetch('/api/sales-performance')
        .then(response => response.json())
        .then(data => {
            if (!data || data.length === 0) {
                console.error('No data received.');
                return;
            }

            // 데이터 키 확인 (부서/팀/직원 구분)
            const keyName = data[0].departmentName ? "departmentName"
                          : data[0].teamName ? "teamName"
                          : "employeeName"; // 기본값은 직원 이름

            // totalSales 기준으로 내림차순 정렬
            const sortedData = data.sort((a, b) => b.totalSales - a.totalSales);

            // 상위 5개와 하위 5개 추출
            const top5Data = sortedData.slice(0, 5);
            const bottom5Data = sortedData.slice(-5);

            // 라벨 및 매출액 추출 (천원 단위 변환)
            const top5Labels = top5Data.map(item => item[keyName]);
            const top5Sales = top5Data.map(item => item.totalSales / 1000);

            const bottom5Labels = bottom5Data.map(item => item[keyName]);
            const bottom5Sales = bottom5Data.map(item => item.totalSales / 1000);

            // 상위 5개 그래프
            createBarChart('topSalesChart', '영업 실적 Top 5', top5Labels, top5Sales, 'rgba(54, 162, 235, 0.2)', 'rgba(54, 162, 235, 1)');

            // 하위 5개 그래프
            createBarChart('bottomSalesChart', '영업 실적 Bottom 5', bottom5Labels, bottom5Sales, 'rgba(255, 99, 132, 0.2)', 'rgba(255, 99, 132, 1)');
        })
        .catch(error => console.error('Error:', error));
});

/**
 * 차트를 생성하는 함수
 */
function createBarChart(canvasId, label, labels, data, bgColor, borderColor) {
    const ctx = document.getElementById(canvasId).getContext('2d');
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: label,
                data: data,
                backgroundColor: bgColor,
                borderColor: borderColor,
                borderWidth: 1
            }]
        },
        options: {
            tooltips: {
                callbacks: {
                    label: function (tooltipItem) {
                        return `${tooltipItem.yLabel.toFixed(1)} k`;
                    }
                }
            },
            scales: {
                yAxes: [{
                    ticks: {
                        beginAtZero: true,
                        callback: function (value) {
                            return value + " k";
                        }
                    }
                }],
                xAxes: [{
                    ticks: {
                        autoSkip: false
                    }
                }]
            }
        }
    });
}
