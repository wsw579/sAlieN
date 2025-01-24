document.addEventListener("DOMContentLoaded", function () {
    const leadStatus = "Under Review"; // 조회하고 싶은 상태 값을 지정

    // REST API 호출
    fetch(`/api/leads/status?leadStatus=${leadStatus}`)
        .then(response => {
            if (!response.ok) {
                throw new Error("Network response was not ok");
            }
            return response.json(); // JSON으로 변환
        })
        .then(data => {
            // API 응답 데이터 가져오기
            const receivedLeadStatus = data.leadStatus; // 서버에서 반환한 leadStatus
            const leadCount = data.leadCount; // 서버에서 반환한 leadCount

            // HTML 요소에 값 삽입
//            const statusElement = document.querySelector("#leadStatus");
            const countElement = document.querySelector("#underReviewLeads");

            countElement.textContent = leadCount; // 개수를 삽입
        })
        .catch(error => console.error("Error fetching leads by status:", error));
});
