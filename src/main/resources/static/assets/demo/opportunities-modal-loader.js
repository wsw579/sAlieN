document.addEventListener("DOMContentLoaded", function () {
    // 진행중 카드 클릭 시 이벤트 추가
    document.getElementById("progressModal").addEventListener("show.bs.modal", function () {
        let modalBody = document.getElementById("progressContent");

        // 기존 내용 초기화 후 로딩 메시지 표시
        modalBody.innerHTML = "<p>데이터를 불러오는 중...</p>";

        // API 요청하여 Mustache 템플릿 로드
        fetch('/api/opportunities/ongoing')
            .then(response => {
                if (!response.ok) {
                    throw new Error("네트워크 오류: " + response.status);
                }
                return response.text();
            })
            .then(html => {
                modalBody.innerHTML = html; // 받은 HTML을 모달 내부에 삽입
            })
            .catch(error => {
                console.error("진행중 기회 목록 로드 실패:", error);
                modalBody.innerHTML = "<p class='text-danger'>데이터를 불러오는 중 오류가 발생했습니다.</p>";
            });
    });
});
