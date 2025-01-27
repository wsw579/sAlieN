document.addEventListener("DOMContentLoaded", function () {
    // REST API 호출
            fetch(`/api/leads/target-close-today`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error("Network response was not ok");
                    }
                    return response.json();
                })
                .then(data => {
                    // API 응답 데이터 가져오기
                    const leadCount = data.leadCount;

                    // 데이터를 HTML 요소에 삽입
                    const targetCloseTodayElement = document.querySelector("#closedLeads");
                    targetCloseTodayElement.textContent = leadCount;
                })
                .catch(error => console.error("Error fetching today's leads:", error));
});