document.addEventListener('DOMContentLoaded', function () {
    // Simple-DataTables
    // https://github.com/fiduswriter/Simple-DataTables/wiki

    const datatablesSimple = document.getElementById('datatablesSimple');
    if (datatablesSimple) {
        new simpleDatatables.DataTable(datatablesSimple,
        {
            serverSide: true, // 서버 사이드 처리 활성화
            paging: false, // 페이지네이션 비활성화
            searching: false, // 검색 활성화 (필요에 따라 설정)
            ordering: false, // 정렬 활성화 (필요에 따라 설정)
            info: false, // 테이블 정보 숨기기 (하단 정보 텍스트 비활성화)
            deferRender: true
        });

        // 검색창 제거
                const searchInput = document.querySelector('.datatable-input');
                        if (searchInput) {
                            searchInput.remove();   // 검색창 DOM에서 삭제
                        }
    }
});



