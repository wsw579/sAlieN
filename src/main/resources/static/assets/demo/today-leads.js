document.addEventListener("DOMContentLoaded", function () {
    // REST API 호출
            fetch(`/api/leads/today`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error("Network response was not ok");
                    }
                    return response.json();
                })
                .then(data => {
                    // API 응답 데이터 가져오기
                    const todayLeadsCount = data.todayLeads;
                    console.log(todayLeadsCount)

                    // HTML 요소에 값 삽입
                    const todayLeadsElement = document.querySelector("#todayLeads");
                    todayLeadsElement.textContent = todayLeadsCount; // 숫자 삽입
                })
                .catch(error => console.error("Error fetching today's leads:", error));
});