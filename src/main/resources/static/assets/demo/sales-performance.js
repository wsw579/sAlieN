document.addEventListener("DOMContentLoaded", function () {
    fetch('/api/sales-performance')
        .then(response => response.json())
        .then(data => {
            // totalSales 기준으로 내림차순 정렬
            const sortedData = data.sort((a, b) => b.totalSales - a.totalSales);

            // 상위 5명과 하위 5명 추출
            const top5Data = sortedData.slice(0, 5);
            const bottom5Data = sortedData.slice(-5);

            // 상위 5명 데이터 (10000으로 나누기)
            const top5Labels = top5Data.map(item => item.employeeName);
            const top5Sales = top5Data.map(item => item.totalSales / 1000); // 천원 단위로 변환

            // 하위 5명 데이터 (10000으로 나누기)
            const bottom5Labels = bottom5Data.map(item => item.employeeName);
            const bottom5Sales = bottom5Data.map(item => item.totalSales / 1000); // 천원 단위로 변환

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
                    tooltips: {
                        callbacks: {
                            label: function (tooltipItem, data) {
                                const value = tooltipItem.yLabel; // y축 값 가져오기
                                return `${value.toFixed(1)} k`; // 툴팁 값에 "k" 추가
                            }
                        }
                    },
                    scales: {
                        yAxes: [{ // 2.x에서는 y축 설정에 yAxes 배열 사용
                            ticks: {
                                beginAtZero: true,
                                callback: function (value, index, values) {
                                    return value + " k"; // 눈금 값 뒤에 "만원" 추가
                                }
                            }
                        }],
                        xAxes: [{ // x축도 xAxes 배열 사용
                            ticks: {
                                autoSkip: false // x축 레이블 자동 생략 방지
                            }
                        }]
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
                    tooltips: {
                        callbacks: {
                            label: function (tooltipItem, data) {
                                const value = tooltipItem.yLabel; // y축 값 가져오기
                                return `${value.toFixed(1)} k`; // 툴팁 값에 "만원" 추가
                            }
                        }
                    },
                    scales: {
                        yAxes: [{ // y축 설정
                            ticks: {
                                beginAtZero: true,
                                callback: function (value) {
                                    return value + " k"; // 눈금 값 뒤에 "만원" 추가
                                }
                            }
                        }],
                        xAxes: [{ // x축 설정
                            ticks: {
                                autoSkip: false
                            }
                        }]
                    }


                }
            });
        })
        .catch(error => console.error('Error:', error));
});
