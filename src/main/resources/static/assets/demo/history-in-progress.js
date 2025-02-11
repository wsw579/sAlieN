document.addEventListener("DOMContentLoaded", function () {
    // API 호출
    fetch(`/history/progress`)
        .then(response => response.json())
        .then(data => {
            const labels = Object.keys(data); // X축: 기회 제목
            const counts = Object.values(data); // Y축: 히스토리 수

            // 그래프 생성
            const ctx = document.getElementById('historyProgress').getContext('2d');
            new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: labels, // 기회 제목
                    datasets: [{
                        label: '히스토리 수',
                        data: counts, // 히스토리 수
                        backgroundColor: 'rgba(75, 192, 192, 0.6)', // 막대 배경색
                        borderColor: 'rgba(75, 192, 192, 1)', // 막대 테두리색
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    scales: {
                        yAxes: [{
                            ticks: {
                                beginAtZero: true // Y축 0부터 시작
                            }
                        }]
                    }
                }
            });
        })
        .catch(error => console.error('Error fetching data:', error));
});
