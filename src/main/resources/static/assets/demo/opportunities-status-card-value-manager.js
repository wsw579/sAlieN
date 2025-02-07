document.addEventListener("DOMContentLoaded", function () {
    // API 호출
    fetch('/api/opportunities/card-value-manager')
        .then(response => response.json()) // JSON 응답 파싱
        .then(data => {
            const statusCounts = data.statusCounts; // "statusCounts" 객체 추출

            // 진행중 값 삽입
            if (statusCounts["Ongoing"]) {
                const ongoingElement = document.getElementById("ongoingCountTeam");
                ongoingElement.textContent = `${statusCounts["Ongoing"]} 건`;
            }

            // 마감일자 초과 값 삽입
            if (statusCounts["Overdue"]) {
                const overdueElement = document.getElementById("overdueCountTeam");
                overdueElement.textContent = `${statusCounts["Overdue"]} 건`;
            }

            // 계약전환 성공 값 삽입
            if (statusCounts["Closed"]) {
                const closedElement = document.getElementById("closedCountTeam");
                closedElement.textContent = `${statusCounts["Closed"]} 건`;
            }

//            // 보류 값 삽입
//            if (statusCounts["Pending"]) {
//                const pendingElement = document.getElementById("pendingCountTeam");
//                pendingElement.textContent = `${statusCounts["Pending"]} 건`;
//            }
        })
        .catch(error => console.error('Error fetching status counts:', error));
});
