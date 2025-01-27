async function fetchLoggedInUser() {
    try {
        const response = await fetch('/api/getLoggedInUser');
        if (!response.ok) {
            throw new Error('Failed to fetch logged-in user');
        }
        return await response.json();
    } catch (error) {
        console.error('Error fetching logged-in user:', error);
        return null;
    }
}

async function initializeChart() {
    const loggedInUser = await fetchLoggedInUser();
    if (!loggedInUser) {
        alert('로그인된 사용자 정보를 가져올 수 없습니다.');
        return;
    }

    const { team, dept } = loggedInUser;

    let apiUrl = '/api/salesData';

    if (team) {
        apiUrl += `?teamId=${team}`;
    } else if (dept) {
        apiUrl += `?departmentId=${dept}`;
    } else {
        alert('팀 ID 또는 부서 ID가 필요합니다.');
        return;
    }


    try {
        const response = await fetch(apiUrl);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();

        const ctx = document.getElementById('teamOpportunitiesChart').getContext('2d');
        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: data.labels,
                datasets: [{
                    label: '기회 수',
                    data: data.values,
                    backgroundColor: 'rgba(54, 162, 235, 0.6)',
                    borderColor: 'rgba(54, 162, 235, 1)',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    } catch (error) {
        console.error('Error fetching sales data:', error.message || error);
        alert('영업 데이터를 가져오는 데 실패했습니다.');
    }
}

initializeChart();
