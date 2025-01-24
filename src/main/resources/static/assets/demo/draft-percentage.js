document.addEventListener("DOMContentLoaded", function () {
    fetch('/api/draft-percentage')
        .then(response => response.json())
        .then(data => {
            const draftPercentage = data.draftPercentage;

            const ctx = document.getElementById('draftChart').getContext('2d');

            // Chart.js 플러그인 정의
            const centerTextPlugin = {
                id: 'centerText',
                beforeDraw(chart) {
                    const { width } = chart;
                    const { height } = chart;
                    const ctx = chart.ctx;

                    ctx.restore();

                    // 첫 번째 줄 (퍼센트)
                    const percentText = `${draftPercentage.toFixed(0)}%`;
                    ctx.font = 'bold 24px Arial'; // 글씨 크기와 폰트 설정
                    ctx.textBaseline = 'middle';
                    ctx.fillStyle = '#000'; // 글씨 색상
                    const textX = Math.round((width - ctx.measureText(percentText).width) / 2);
                    const textY = height / 2 - 10; // 위로 약간 올림
                    ctx.fillText(percentText, textX, textY);

                    // 두 번째 줄 (설명 텍스트)
                    const labelText = 'Close 비율';
                    ctx.font = '14px Arial';
                    ctx.fillStyle = '#666';
                    const labelX = Math.round((width - ctx.measureText(labelText).width) / 2);
                    const labelY = height / 2 + 15; // 아래로 약간 내림
                    ctx.fillText(labelText, labelX, labelY);

                    ctx.save();
                }
            };

            // 도넛 그래프 생성
            new Chart(ctx, {
                type: 'doughnut',
                data: {
                    datasets: [{
                        data: [draftPercentage, 100 - draftPercentage],
                        backgroundColor: ['#17a2b8', '#d4f1f9'], // 도넛 색상
                        borderWidth: 0, // 테두리 제거
                    }]
                },
                options: {
                    responsive: true,
                    cutout: '50%', // 도넛 두께 조정
                    plugins: {
                        legend: {
                            display: false // 범례 숨김
                        },
                        tooltip: {
                            enabled: false // 툴팁 숨김
                        }
                    }
                },
                plugins: [centerTextPlugin] // 플러그인 등록
            });
        })
        .catch(error => console.error('Error:', error));
});
